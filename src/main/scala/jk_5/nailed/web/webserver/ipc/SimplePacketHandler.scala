package jk_5.nailed.web.webserver.ipc

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.channel.ChannelHandler.Sharable
import jk_5.nailed.web.webserver.ipc.packet.IpcPacket

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object SimplePacketHandler extends SimpleChannelInboundHandler[IpcPacket] {
  override def messageReceived(ctx: ChannelHandlerContext, msg: IpcPacket) = msg.processPacket()
}
