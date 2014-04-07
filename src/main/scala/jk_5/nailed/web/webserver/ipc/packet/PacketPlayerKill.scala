package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.game.GameServer
import jk_5.nailed.web.webserver.ipc.PacketUtils

/**
 * No description given
 *
 * @author jk-5
 */
class PacketPlayerKill extends IpcPacket {

  private var killer: String = _
  private var victim: String = _

  override def encode(buffer: ByteBuf){}
  override def decode(buffer: ByteBuf){
    this.killer = PacketUtils.readString(buffer)
    this.victim = PacketUtils.readString(buffer)
  }
  override def processPacket(server: GameServer){
    val k = server.getPlayer(this.killer)
    val v = server.getPlayer(this.victim)

    println(s"Player ${k.get.name} killed ${v.get.name}")
  }
}
