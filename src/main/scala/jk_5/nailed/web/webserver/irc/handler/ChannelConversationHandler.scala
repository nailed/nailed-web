package jk_5.nailed.web.webserver.irc.handler

import jk_5.nailed.web.webserver.irc.{ProtocolIrc, IrcConnection}
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}

/**
 * No description given
 *
 * @author jk-5
 */
class ChannelConversationHandler(val connection: IrcConnection) extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = msg match {
    case frame: String =>
      println(frame)
      val operation = frame.substring(0, frame.indexOf(' '))
      val args = frame.substring(frame.indexOf(' ') + 1)
      operation match {
        case "QUIT" => this.connection.disconnected(args)
        case "JOIN" => args.split(",").foreach(c => this.connection join ProtocolIrc.getOrCreateChannel(c))
        case _ => this.connection.channel.writeAndFlush(s":${ProtocolIrc.host} 421 ${this.connection.nickname} $operation :Unknown command")
      }
    case m => ctx.fireChannelRead(m)
  }
}
