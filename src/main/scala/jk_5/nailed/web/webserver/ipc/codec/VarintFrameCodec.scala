package jk_5.nailed.web.webserver.ipc.codec

import io.netty.handler.codec.{CorruptedFrameException, ByteToMessageCodec}
import io.netty.buffer.{Unpooled, ByteBuf}
import io.netty.channel.ChannelHandlerContext
import java.util
import jk_5.nailed.web.webserver.ipc.PacketUtils

/**
 * No description given
 *
 * @author jk-5
 */
class VarintFrameCodec extends ByteToMessageCodec[ByteBuf] {
  override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]){
    in.markReaderIndex
    val buf = new Array[Byte](3)
    for(i <- 0 until buf.length){
      if(!in.isReadable) {
        in.resetReaderIndex
        return
      }
      buf(i) = in.readByte
      if(buf(i) >= 0) {
        val length: Int = PacketUtils.readVarInt(Unpooled.wrappedBuffer(buf))
        if(in.readableBytes < length) {
          in.resetReaderIndex
          return
        }else{
          out.add(in.readBytes(length))
          return
        }
      }
    }
    throw new CorruptedFrameException("Length wider than 21-bit")
  }
  override def encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf){
    val bodyLength = msg.readableBytes
    val headerLength = PacketUtils.varIntSize(bodyLength)
    out.ensureWritable(headerLength + bodyLength)
    PacketUtils.writeVarInt(bodyLength, out)
    out.writeBytes(msg)
  }
}
