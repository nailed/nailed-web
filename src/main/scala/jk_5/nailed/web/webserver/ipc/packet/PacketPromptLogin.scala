package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.game.{Player, GameServer}
import jk_5.nailed.web.webserver.ipc.PacketUtils

/**
 * No description given
 *
 * @author jk-5
 */
class PacketPromptLogin extends IpcPacket {

  var player: Player = _

  override def encode(buffer: ByteBuf){
    PacketUtils.writeString(this.player.id, buffer)
  }

  override def decode(buffer: ByteBuf){}
  override def processPacket(server: GameServer){}
}
