package jk_5.nailed.web.webserver.socketio.packet

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import jk_5.nailed.web.webserver.socketio.{ClientRegistry, HeartbeatHandler}
import io.netty.channel.ChannelHandler.Sharable
import org.apache.logging.log4j.LogManager

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object SIOPacketHandler extends SimpleChannelInboundHandler[SIOPacket] {

  val logger = LogManager.getLogger
  override def messageReceived(ctx: ChannelHandlerContext, msg: SIOPacket){
    val client = ctx.channel().attr(ClientRegistry.sioClient).get()
    msg match {
      case p: HeartbeatSIOPacket => HeartbeatHandler.onHeartbeat(client)
      case p => this.logger.warn("Unhandled packet type " + p.getType)
    }
  }

  override def channelInactive(ctx: ChannelHandlerContext){
    val sioClient = ctx.channel().attr(ClientRegistry.sioClient).getAndRemove
    if(sioClient != null){
      ClientRegistry.disconnect(sioClient)
    }
    ctx.fireChannelInactive()
  }
}
