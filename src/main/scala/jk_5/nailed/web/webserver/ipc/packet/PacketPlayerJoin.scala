package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.game.{Player, GameServer}
import jk_5.nailed.web.webserver.ipc.PacketUtils

/**
 * No description given
 *
 * @author jk-5
 */
class PacketPlayerJoin extends IpcPacket {

  var player: Player = _

  override def encode(buffer: ByteBuf){}
  override def decode(buffer: ByteBuf){
    this.player = new Player(PacketUtils.readString(buffer), PacketUtils.readString(buffer), PacketUtils.readString(buffer))
  }
  override def processPacket(server: GameServer){
    server.onPlayerJoin(this.player)

    val p = new PacketPromptLogin
    p.player = this.player
    server.sendPacket(p)
  }
}
