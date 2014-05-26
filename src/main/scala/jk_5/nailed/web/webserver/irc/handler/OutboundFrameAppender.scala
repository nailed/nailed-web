package jk_5.nailed.web.webserver.irc.handler

import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.channel.ChannelHandlerContext
import java.util
import io.netty.channel.ChannelHandler.Sharable
import org.apache.logging.log4j.LogManager

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object OutboundFrameAppender extends MessageToMessageEncoder[String] {
  val logger = LogManager.getLogger

  override def encode(ctx: ChannelHandlerContext, msg: String, out: util.List[AnyRef]){
    val frame = msg + "\r\n"
    if(frame.length > 512) throw new Exception(s"Outbound frame length is ${frame.length} (> 512)")
    out.add(frame)
  }
}
