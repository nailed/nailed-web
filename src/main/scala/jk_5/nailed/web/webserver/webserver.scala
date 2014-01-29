package jk_5.nailed.web.webserver

import io.netty.channel.{ChannelFuture, ChannelFutureListener, ChannelInitializer}
import io.netty.channel.socket.SocketChannel
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.{HttpResponseEncoder, HttpRequestDecoder}

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
  def initChannel(ch: SocketChannel){
    val pipe = ch.pipeline()

    pipe.addLast("httpDecoder", new HttpRequestDecoder)
    pipe.addLast("httpEncoder", new HttpResponseEncoder)
  }
}
