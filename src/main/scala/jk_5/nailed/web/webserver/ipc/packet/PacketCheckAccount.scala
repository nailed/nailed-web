package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.game.GameServer
import jk_5.nailed.web.webserver.ipc.PacketUtils
import jk_5.nailed.web.auth.UserDatabase

/**
 * No description given
 *
 * @author jk-5
 */
class PacketCheckAccount extends IpcPacket {

  var playerId: String = _
  var data: String = _
  var typ = -1

  override def encode(buffer: ByteBuf){
    PacketUtils.writeString(this.playerId, buffer)
    PacketUtils.writeString(this.data, buffer)
    buffer.writeByte(this.typ)
  }

  override def decode(buffer: ByteBuf){
    this.playerId = PacketUtils.readString(buffer)
    this.data = PacketUtils.readString(buffer)
    this.typ = buffer.readByte
  }

  override def processPacket(server: GameServer){
    val res = new PacketCheckAccount
    res.playerId = this.playerId
    res.typ = this.typ
    if(this.typ == 0){ //Username
      if(UserDatabase.getUserByUsername(this.data).isDefined){
        res.data = "used"
      }else{
        res.data = "free"
      }
    }else if(this.typ == 1){ //Email
      if(UserDatabase.getUserByEmail(this.data).isDefined){
        res.data = "used"
      }else{
        res.data = "free"
      }
    }
    server.sendPacket(res)
  }
}
