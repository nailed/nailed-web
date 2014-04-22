package jk_5.nailed.web.webserver.http

import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import jk_5.nailed.web.webserver.irc.{IrcConnection, ProtocolIrc}
import io.netty.handler.codec.http.HttpRequest
import io.netty.channel.ChannelHandler.Sharable

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object HttpRequestLogger extends ChannelInboundHandlerAdapter {

  val channel = ProtocolIrc.getOrCreateChannel("#httplog")
  val conn = new IrcConnection() {
    this.setAllNames("httplogger")
  }
  conn.join(channel)
  conn.setMode(channel, "+o")

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any){
    msg match {
      case e: HttpRequest => this.channel.onMessage(conn, s"${e.getMethod} ${e.getUri}")
      case _ =>
    }
    ctx.fireChannelRead(msg)
  }
}
