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
  override def channelRead0(ctx: ChannelHandlerContext, msg: IpcPacket){
    var gameserver = Option(ctx.channel().attr(ProtocolIpc.gameServer).get())
    if(gameserver.isEmpty){
      val srv = new FakeGameServer(ctx.channel())
      ctx.channel().attr(ProtocolIpc.gameServer).set(srv)
      gameserver = Some(srv)
    }
    msg.processPacket(gameserver.get)
  }
}
