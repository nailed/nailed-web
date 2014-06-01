package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.webserver.ipc.PacketUtils
import jk_5.nailed.web.game.GameServer
import jk_5.nailed.web.chat.ChatHandler

/**
 * No description given
 *
 * @author jk-5
 */
class PacketChatIn extends IpcPacket {

  var playerId: String = _
  var message: String = _
  var targetType: Int = _
  var target: String = _

  override def encode(buffer: ByteBuf){

  }

  override def decode(buffer: ByteBuf){
    this.playerId = PacketUtils.readString(buffer)
    this.message = PacketUtils.readString(buffer)
    this.targetType = buffer.readByte
    this.target = PacketUtils.readString(buffer)
  }

  override def processPacket(server: GameServer){
    server.getPlayer(this.playerId) match {
      case Some(player) => this.targetType match {
        case 0 =>
          ChatHandler.sendGlobalChat(player, this.message)
        case _ =>
      }
      case _ =>
    }
  }
}
