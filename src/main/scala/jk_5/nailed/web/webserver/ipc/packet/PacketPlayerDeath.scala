package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.game.GameServer
import jk_5.nailed.web.webserver.ipc.PacketUtils

/**
 * No description given
 *
 * @author jk-5
 */
class PacketPlayerDeath extends IpcPacket {

  var id: String = _
  var cause: String = _

  override def encode(buffer: ByteBuf){}
  override def decode(buffer: ByteBuf){
    this.id = PacketUtils.readString(buffer)
    this.cause = PacketUtils.readString(buffer)
  }
  override def processPacket(server: GameServer){
    val player = server.getPlayer(this.id)
    println(s"Player ${player.get.name} died $cause")
  }
}
