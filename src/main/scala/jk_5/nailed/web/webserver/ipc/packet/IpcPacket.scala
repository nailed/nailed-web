package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.game.GameServer

/**
 * No description given
 *
 * @author jk-5
 */
abstract class IpcPacket {
  def encode(buffer: ByteBuf)
  def decode(buffer: ByteBuf)
  def processPacket(server: GameServer)
}
