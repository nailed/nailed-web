package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.webserver.ipc.PacketUtils
import jk_5.nailed.web.game.{Player, GameServer}

/**
 * No description given
 *
 * @author jk-5
 */
class PacketLoginResponse extends IpcPacket {

  var player: Player = _
  var state = -1 //0 = OK, 1 = Wrong Username, 2 = Wrong Password, 3 = Unknown Player

  override def encode(buffer: ByteBuf){
    buffer.writeBoolean(this.player != null)
    if(this.player != null) PacketUtils.writeString(this.player.id, buffer)
    buffer.writeByte(this.state)
  }

  override def decode(buffer: ByteBuf){}
  override def processPacket(server: GameServer){}
}
