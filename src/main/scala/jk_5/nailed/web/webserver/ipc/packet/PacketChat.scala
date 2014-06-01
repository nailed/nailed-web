package jk_5.nailed.web.webserver.ipc.packet

import jk_5.nailed.web.game.GameServer
import io.netty.buffer.ByteBuf
import jk_5.nailed.web.webserver.ipc.PacketUtils

/**
 * No description given
 *
 * @author jk-5
 */
class PacketChat extends IpcPacket {

  var destId: String = _
  var message: String = _

  override def encode(buffer: ByteBuf){
    PacketUtils.writeString(destId, buffer)
    PacketUtils.writeString(message, buffer)
  }

  override def decode(buffer: ByteBuf){

  }

  override def processPacket(server: GameServer){

  }
}
