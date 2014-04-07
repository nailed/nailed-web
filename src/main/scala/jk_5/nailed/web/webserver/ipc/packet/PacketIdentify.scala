package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.game.GameServer

/**
 * No description given
 *
 * @author jk-5
 */
class PacketIdentify extends IpcPacket {

  override def encode(buffer: ByteBuf){}
  override def decode(buffer: ByteBuf){}
  override def processPacket(server: GameServer){}
}
