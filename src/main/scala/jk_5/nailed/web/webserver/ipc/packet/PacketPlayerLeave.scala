package jk_5.nailed.web.webserver.ipc.packet

import jk_5.nailed.web.game.GameServer
import jk_5.nailed.web.webserver.ipc.PacketUtils
import io.netty.buffer.ByteBuf

/**
  * No description given
  *
  * @author jk-5
  */
class PacketPlayerLeave extends IpcPacket {

  var id: String = _

  override def encode(buffer: ByteBuf){}
  override def decode(buffer: ByteBuf){
    this.id = PacketUtils.readString(buffer)
  }
  override def processPacket(server: GameServer){
    server.getPlayer(this.id).foreach(p => server.onPlayerLeave(p))
  }
}
