package jk_5.nailed.web.webserver

import io.netty.channel._
import io.netty.channel.socket.SocketChannel
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.ChannelHandler.Sharable
import io.netty.handler.timeout.{ReadTimeoutHandler, ReadTimeoutException}
import jk_5.nailed.web.webserver.http.{MultiplexingUrlResolver, ProtocolHttp}
import jk_5.nailed.web.webserver.http.handlers._
import jk_5.nailed.web.webserver.ipc.ProtocolIpc
import org.apache.logging.log4j.LogManager
import jk_5.nailed.web.webserver.socketio.handler.{WebServerHandlerFlashResources, WebServerHandlerSIOHandshake}
import jk_5.nailed.web.webserver.socketio.transport.websocket.WebServerHandlerSIOWebSocket
import jk_5.nailed.web.webserver.irc.ProtocolIrc

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
      def operationComplete(future: ChannelFuture) = logger.info(s"Webserver running on port $port!")
    })
  }
}

object Pipeline extends ChannelInitializer[SocketChannel] {

  val webserverMultiplexer = new MultiplexingUrlResolver

  ProtocolMultiplexer.addHandler(ProtocolIrc)
  ProtocolMultiplexer.addHandler(ProtocolHttp)
  ProtocolMultiplexer.addHandler(ProtocolIpc)
  ProtocolMultiplexer.addHandler(ProtocolFlashPolicy)

  this.webserverMultiplexer.addHandler("/api/mappacks.json", classOf[WebServerHandlerMappackList])
  this.webserverMultiplexer.addHandler("/api/mappacks/(.*).json", classOf[WebServerHandlerMappackData])
  this.webserverMultiplexer.addHandler("/api/login/", classOf[WebServerHandlerLogin])
  this.webserverMultiplexer.addHandler("/api/register/", classOf[WebServerHandlerRegister])
  this.webserverMultiplexer.addHandler("/api/link/", classOf[WebServerHandlerLinkMojang])
  this.webserverMultiplexer.addHandler("/api/servers.json", classOf[WebServerHandlerServerList])
  this.webserverMultiplexer.addHandler("/socket.io/static/flashsocket/(.*).swf", classOf[WebServerHandlerFlashResources])
  this.webserverMultiplexer.addHandler("/socket.io/([0-9]+)/websocket/([0-9a-z]+)", classOf[WebServerHandlerSIOWebSocket])
  this.webserverMultiplexer.addHandler("/socket.io/([0-9]+)/flashsocket/([0-9a-z]+)", classOf[WebServerHandlerSIOWebSocket])
  this.webserverMultiplexer.addHandler("/socket.io/([0-9]+)/", classOf[WebServerHandlerSIOHandshake])
  this.webserverMultiplexer.addHandler("/(.*)", classOf[WebServerHandlerHtml])

  val router = new RouterHandler(this.webserverMultiplexer, "routedHandler")

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
