package jk_5.nailed.web.webserver.ipc.packet

import jk_5.nailed.web.game.{Player, GameServer}
import jk_5.nailed.web.webserver.ipc.PacketUtils
import io.netty.buffer.ByteBuf

/**
  * No description given
  *
  * @author jk-5
  */
class PacketPlayerLeave extends IpcPacket {

  var player: Player = _

  override def encode(buffer: ByteBuf){}
  override def decode(buffer: ByteBuf){
    this.player = new Player(PacketUtils.readString(buffer), PacketUtils.readString(buffer))
  }
  override def processPacket(server: GameServer){
    server.onPlayerLeave(this.player)
  }
}
