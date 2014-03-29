package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf

/**
 * No description given
 *
 * @author jk-5
 */
abstract class IpcPacket {
  def encode(buffer: ByteBuf)
  def decode(buffer: ByteBuf)
  def processPacket()
}
