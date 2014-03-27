package jk_5.nailed.web.webserver.ipc

import io.netty.util.CharsetUtil
import io.netty.buffer.ByteBuf

/**
 * No description given
 *
 * @author jk-5
 */
object PacketUtils {
  def readVarInt(input: ByteBuf): Int ={
    var out: Int = 0
    var bytes: Int = 0
    var in: Byte = 0
    while(true) {
      in = input.readByte
      out |= (in & 127) << ({
        bytes += 1
        bytes - 1
      } * 7)
      if(bytes > 5) {
        throw new RuntimeException("VarInt too big")
      }
      if((in & 0x80) != 0x80) {
        return out
      }
    }
    out
  }

  def writeVarInt(v: Int, output: ByteBuf){
    var value = v
    var part: Int = 0
    while(true) {
      part = value & 0x7F
      value >>>= 7
      if(value != 0) {
        part |= 0x80
      }
      output.writeByte(part)
      if(value == 0) {
        return
      }
    }
  }

  def varIntSize(varint: Int): Int ={
    if((varint & 0xFFFFFF80) == 0) return 1
    if((varint & 0xFFFFC000) == 0) return 2
    if((varint & 0xFFE00000) == 0) return 3
    if((varint & 0xF0000000) == 0) return 4
    5
  }

  def readString(buffer: ByteBuf): String ={
    val len = readVarInt(buffer)
    val str = buffer.toString(buffer.readerIndex, len, CharsetUtil.UTF_8)
    buffer.readerIndex(buffer.readerIndex + len)
    str
  }

  def writeString(string: String, buffer: ByteBuf){
    val bytes = string.getBytes(CharsetUtil.UTF_8)
    writeVarInt(bytes.length, buffer)
    buffer.writeBytes(bytes)
  }
}
