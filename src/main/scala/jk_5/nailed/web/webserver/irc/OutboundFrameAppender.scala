package jk_5.nailed.web.webserver.irc

import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.channel.ChannelHandlerContext
import java.util
import io.netty.channel.ChannelHandler.Sharable

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object OutboundFrameAppender extends MessageToMessageEncoder[String] {
  override def encode(ctx: ChannelHandlerContext, msg: String, out: util.List[AnyRef]){
    out.add(msg + "\n")
  }
}
