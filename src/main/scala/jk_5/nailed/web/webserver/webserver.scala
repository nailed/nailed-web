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

/**
 * No description given
 *
 * @author jk-5
 */
object WebServer {
  val boss = new NioEventLoopGroup()
  val worker = new NioEventLoopGroup()

  def start(){
    println("Starting webserver on port 9001")
    val b = new ServerBootstrap().group(this.boss, this.worker).channel(classOf[NioServerSocketChannel]).childHandler(Pipeline)
    b.localAddress("0.0.0.0", 9001)
    b.bind().addListener(new ChannelFutureListener {
      def operationComplete(future: ChannelFuture) = println("Webserver running!")
    })
  }
}

object Pipeline extends ChannelInitializer[SocketChannel] {

  val webserverMultiplexer = new MultiplexingUrlResolver

  ProtocolMultiplexer.addHandler(ProtocolHttp)
  ProtocolMultiplexer.addHandler(ProtocolIpc)

  this.webserverMultiplexer.addHandler("/api/mappacks.json", classOf[WebServerHandlerMappackList])
  this.webserverMultiplexer.addHandler("/api/mappacks/(.*).json", classOf[WebServerHandlerMappackData])
  this.webserverMultiplexer.addHandler("/api/login/", classOf[WebServerHandlerLogin])
  this.webserverMultiplexer.addHandler("/api/register/", classOf[WebServerHandlerRegister])
  this.webserverMultiplexer.addHandler("/api/link/", classOf[WebServerHandlerLinkMojang])
  this.webserverMultiplexer.addHandler("/(.*)", classOf[WebServerHandlerHtml])

  val router = new RouterHandler(this.webserverMultiplexer, "routedHandler")

  def initChannel(ch: SocketChannel){
    val pipe = ch.pipeline()

    //pipe.addLast("sslDetector", new SslDetector)
    pipe.addLast("timeoutHandler", new ReadTimeoutHandler(10))
    pipe.addLast("timeoutDetector", ReadTimeoutDetector)
    pipe.addLast("protocolMultiplexer", new ProtocolMultiplexer)
  }
}

@Sharable
object ReadTimeoutDetector extends ChannelHandlerAdapter {
  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable){
    cause match{
      case e: ReadTimeoutException => ctx.close()
      case e => ctx.fireExceptionCaught(e)
    }
  }
}
