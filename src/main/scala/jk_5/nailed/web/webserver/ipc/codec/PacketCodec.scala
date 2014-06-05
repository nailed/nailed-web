package jk_5.nailed.web.webserver.ipc.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import jk_5.nailed.web.webserver.ipc.packet._
import scala.collection.mutable
import java.util
import jk_5.nailed.web.webserver.ipc.ProtocolIpc
import jk_5.nailed.web.game.ServerRegistry

/**
 * No description given
 *
 * @author jk-5
 */
class PacketCodec extends ByteToMessageCodec[IpcPacket] {

  private final val idToClass = mutable.HashMap[Byte, Class[_ <: IpcPacket]]()
  private final val classToId = mutable.HashMap[Class[_ <: IpcPacket], Byte]()

  this.registerPacket(0, classOf[PacketIdentify])
  this.registerPacket(1, classOf[PacketInitConnection])
  this.registerPacket(2, classOf[PacketPlayerJoin])
  this.registerPacket(3, classOf[PacketPlayerLeave])
  this.registerPacket(4, classOf[PacketPlayerDeath])
  this.registerPacket(5, classOf[PacketPlayerKill])
  this.registerPacket(6, classOf[PacketPromptLogin])
  this.registerPacket(7, classOf[PacketLoginPlayer])
  this.registerPacket(8, classOf[PacketLoginResponse])
  this.registerPacket(9, classOf[PacketCheckAccount])
  this.registerPacket(10, classOf[PacketCreateAccount])
  this.registerPacket(11, classOf[PacketUserdata])
  this.registerPacket(12, classOf[PacketLoadMappackMeta])
  this.registerPacket(13, classOf[PacketChatIn])
  this.registerPacket(14, classOf[PacketChat])
  this.registerPacket(15, classOf[PacketListMappacks])

  private def registerPacket(id: Byte, packet: Class[_ <: IpcPacket]): PacketCodec ={
    this.idToClass.put(id, packet)
    this.classToId.put(packet, id)
    this
  }

  protected def encode(ctx: ChannelHandlerContext, msg: IpcPacket, out: ByteBuf){
    val cl = msg.getClass
    if(!this.classToId.contains(cl)){
      throw new UnsupportedOperationException("Trying to send an unregistered packet (" + cl.getSimpleName + ")")
    }
    val id = this.classToId.get(cl).get
    out.writeByte(id)
    msg.encode(out)
  }

  protected def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]){
    val id = in.readByte
    if(!this.idToClass.contains(id)){
      throw new UnsupportedOperationException("Received an unknown packet (id: " + id + ")")
    }
    val packet = this.idToClass.get(id).get.newInstance()
    packet.decode(in)
    out.add(packet)
  }

  override def channelInactive(ctx: ChannelHandlerContext){
    ProtocolIpc.logger.trace(ProtocolIpc.connectionMarker, "IPC Connection closed")
    val srv = ctx.channel().attr(ProtocolIpc.gameServer).getAndRemove
    ServerRegistry.removeServer(srv)
    ctx.fireChannelInactive()
  }
}
