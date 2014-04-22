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
  val connection = new IrcConnection("httplogger")
  connection.join(channel)
  channel.setMode(connection, "+q")

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any){
    msg match {
      case e: HttpRequest =>
        ctx.fireChannelRead(e)
        connection.sendMessage(channel, s"${e.getMethod} ${e.getUri}")
      case e =>
        ctx.fireChannelRead(e)
    }
  }
}
