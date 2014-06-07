package jk_5.nailed.web.webserver

import io.netty.channel._
import io.netty.channel.socket.SocketChannel
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.ChannelHandler.Sharable
import io.netty.handler.timeout.ReadTimeoutHandler
import jk_5.nailed.web.webserver.http.ProtocolHttp
import jk_5.nailed.web.webserver.ipc.ProtocolIpc
import jk_5.nailed.web.webserver.irc.ProtocolIrc
import org.apache.logging.log4j.LogManager
import java.io.IOException
import io.netty.handler.codec.DecoderException
import javax.net.ssl.SSLException

/**
 * No description given
 *
 * @author jk-5
 */
object WebServer {
  val boss = new NioEventLoopGroup(2)
  val worker = new NioEventLoopGroup()
  val logger = LogManager.getLogger

  def start(){
    this.startOnPort(6667, ProtocolIrc)
    this.startOnPort(9001, ProtocolHttp)
    this.startOnPort(9002, ProtocolIpc)
  }

  def startOnPort(port: Int, protocol: ServerProtocol){
    this.logger.info(s"Starting webserver on port $port")
    /*val b = new ServerBootstrap().group(this.boss, this.worker).channel(classOf[NioServerSocketChannel]).childHandler(new ChannelInitializer[Channel] {
      override def initChannel(ch: Channel){
        protocol.configureChannel(ch)
      }
    })*/
    val b = new ServerBootstrap().group(this.boss, this.worker).channel(classOf[NioServerSocketChannel]).childHandler(Pipeline)
    b.localAddress("0.0.0.0", port)
    b.bind().addListener(new ChannelFutureListener {
      def operationComplete(future: ChannelFuture) = logger.info(s"Webserver running on port $port")
    })
  }
}

/*
 * A request consists of the following objects:
 *  - HttpRequest (Headers and stuff)
 *  - HttpContent (Data chunks (Only for POST, PUT and PATCH requests)
 *  - LastHttpContent (The end of the request)
 */

object Pipeline extends ChannelInitializer[SocketChannel] {

  ProtocolMultiplexer.addHandler(ProtocolIrc)
  ProtocolMultiplexer.addHandler(ProtocolHttp)
  ProtocolMultiplexer.addHandler(ProtocolIpc)
  ProtocolMultiplexer.addHandler(ProtocolFlashPolicy)

  def initChannel(ch: SocketChannel){
    val pipe = ch.pipeline()

    pipe.addLast("sslDetector", new SslDetector)
    //pipe.addLast("logger", new LoggingHandler(LogLevel.INFO))
    pipe.addLast("protocolMultiplexer", new ProtocolMultiplexer)
    pipe.addLast("exceptionHandler", ExceptionHandler)
  }
}

@Sharable
object ExceptionHandler extends ChannelHandlerAdapter {
  val logger = LogManager.getLogger
  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) = cause match {
    case e: IOException => e.getCause match {
      case e1: SSLException => //Fully ignore these
      case e1 => silentIgnore(e1)
    }
    case e: DecoderException => e.getCause match {
      case e1: SSLException =>
      case _ => exception(e)
    }
    case e => exception(e)
  }

  def silentIgnore(t: Throwable){
    logger.trace(s"Silently ignored ${t.getClass.getSimpleName} in pipeline (${t.getMessage})")
  }

  def exception(t: Throwable){
    logger.error("Caught error in pipeline", t)
  }
}
