package jk_5.nailed.web.webserver

import io.netty.channel.{Channel, ChannelFutureListener}
import io.netty.buffer.{Unpooled, ByteBuf}
import io.netty.util.CharsetUtil

/**
 * No description given
 *
 * @author jk-5
 */
object ProtocolFlashPolicy extends ServerProtocol {
  private final val requestBuffer = Unpooled.copiedBuffer("<policy-file-request/>", CharsetUtil.UTF_8)
  private final val responseBuffer = Unpooled.copiedBuffer(
    "<?xml version=\"1.0\"?>" +
    "<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">" +
    "<cross-domain-policy>" +
    "   <site-control permitted-cross-domain-policies=\"master-only\"/>" +
    "   <allow-access-from domain=\"*\" to-ports=\"*\" />" +
    "</cross-domain-policy>", CharsetUtil.UTF_8)

  override def matches(buffer: ByteBuf): Boolean = {
    if(buffer.readableBytes() >= this.requestBuffer.readableBytes()){
      buffer.slice(0, this.requestBuffer.readableBytes()) equals this.requestBuffer
    }else false
  }
  override def configureChannel(channel: Channel){
    channel.writeAndFlush(Unpooled.copiedBuffer(this.responseBuffer)).addListener(ChannelFutureListener.CLOSE)
  }
}
