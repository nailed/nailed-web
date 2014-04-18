package jk_5.nailed.web.webserver

import io.netty.channel._
import io.netty.channel.socket.SocketChannel
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.ChannelHandler.Sharable
import io.netty.handler.timeout.{ReadTimeoutHandler, ReadTimeoutException}
import jk_5.nailed.web.webserver.http.ProtocolHttp
import jk_5.nailed.web.webserver.ipc.ProtocolIpc
import jk_5.nailed.web.webserver.irc.ProtocolIrc
import org.apache.logging.log4j.LogManager
import io.netty.handler.logging.LoggingHandler

/**
 * No description given
 *
 * @author jk-5
 */
object WebServer {
  val boss = new NioEventLoopGroup()
  val worker = new NioEventLoopGroup()
  val logger = LogManager.getLogger

  def start(){
    this.startOnPort(6667)
    this.startOnPort(9001)
  }

  def startOnPort(port: Int){
    this.logger.info(s"Starting webserver on port $port")
    val b = new ServerBootstrap().group(this.boss, this.worker).channel(classOf[NioServerSocketChannel]).childHandler(Pipeline)
    b.localAddress("0.0.0.0", port)
    b.bind().addListener(new ChannelFutureListener {
      def operationComplete(future: ChannelFuture) = logger.info(s"Webserver running on port $port")
    })
  }
}

object Pipeline extends ChannelInitializer[SocketChannel] {

  ProtocolMultiplexer.addHandler(ProtocolIrc)
  ProtocolMultiplexer.addHandler(ProtocolHttp)
  ProtocolMultiplexer.addHandler(ProtocolIpc)
  ProtocolMultiplexer.addHandler(ProtocolFlashPolicy)

  def initChannel(ch: SocketChannel){
    val pipe = ch.pipeline()

    pipe.addLast("sslDetector", new SslDetector)
    //pipe.addLast("logger", new LoggingHandler(LogLevel.INFO))
    pipe.addLast("timeoutHandler", new ReadTimeoutHandler(10))
    pipe.addLast("timeoutDetector", ReadTimeoutDetector)
    pipe.addLast("protocolMultiplexer", new ProtocolMultiplexer)
  }
}

@Sharable
object ReadTimeoutDetector extends ChannelHandlerAdapter {
  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) = cause match{
    case e: ReadTimeoutException => ctx.close()
    case e => ctx.fireExceptionCaught(e)
  }
}
