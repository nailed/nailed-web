package jk_5.nailed.web.webserver.ipc.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import jk_5.nailed.web.webserver.ipc.packet.{PacketInitConnection, IpcPacket}
import scala.collection.mutable
import java.util

/**
 * No description given
 *
 * @author jk-5
 */
class PacketCodec extends ByteToMessageCodec[IpcPacket] {

  private final val idToClass = mutable.HashMap[Byte, Class[_ <: IpcPacket]]()
  private final val classToId = mutable.HashMap[Class[_ <: IpcPacket], Byte]()

  this.registerPacket(1, classOf[PacketInitConnection])

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
    val id: Byte = in.readByte
    if(!this.idToClass.contains(id)){
      throw new UnsupportedOperationException("Received an unknown packet (id: " + id + ")")
    }
    val packet = this.idToClass.get(id).get.newInstance()
    packet.decode(in)
    out.add(packet)
  }
}
