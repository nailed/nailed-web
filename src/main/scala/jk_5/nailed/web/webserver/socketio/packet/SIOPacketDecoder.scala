package jk_5.nailed.web.webserver.socketio.packet

import io.netty.handler.codec.{DecoderException, MessageToMessageDecoder}
import io.netty.channel.ChannelHandlerContext
import java.util
import io.netty.channel.ChannelHandler.Sharable

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object SIOPacketDecoder extends MessageToMessageDecoder[EncodedSIOPacket] {
  override def decode(ctx: ChannelHandlerContext, msg: EncodedSIOPacket, out: util.List[AnyRef]){
    if(msg.data.length() < 3){
      throw new DecoderException("Can\'t parse this frame")
    }
    val typ = PacketType.valueOf(msg.data.charAt(0).toString.toInt)
    typ match {
      case PacketType.DISCONNECT => out.add(new DisconnectSIOPacket)
      case PacketType.CONNECT => out.add(new ConnectSIOPacket)
      case PacketType.HEARTBEAT => out.add(new HeartbeatSIOPacket)
      case PacketType.MESSAGE => out.add(new MessageSIOPacket())
      case PacketType.JSON => out.add(new JsonSIOPacket)
      case PacketType.EVENT => out.add(new EventSIOPacket)
      case PacketType.ACK => out.add(new AckSIOPacket)
      case PacketType.ERROR => out.add(new ErrorSIOPacket)
      case PacketType.NOOP => out.add(new NoopSIOPacket)
      case t => throw new DecoderException("Unknown packet type " + t)
    }
  }
}
