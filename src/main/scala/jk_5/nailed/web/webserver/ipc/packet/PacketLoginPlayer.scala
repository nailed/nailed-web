package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.game.GameServer
import jk_5.nailed.web.webserver.ipc.PacketUtils
import jk_5.nailed.web.auth.{SessionManager, UserDatabase}

/**
 * No description given
 *
 * @author jk-5
 */
class PacketLoginPlayer extends IpcPacket {

  var playerId: String = null
  var username: String = null
  var password: String = null

  override def encode(buffer: ByteBuf){}

  override def decode(buffer: ByteBuf){
    this.playerId = PacketUtils.readString(buffer)
    this.username = PacketUtils.readString(buffer)
    this.password = PacketUtils.readString(buffer)
  }

  override def processPacket(server: GameServer){
    val u = UserDatabase.getUserByUsername(this.username)
    val response = new PacketLoginResponse
    val p = server.getPlayer(this.playerId)
    if(p.isEmpty){
      response.state = 3
      server.sendPacket(response)
      return
    }
    response.player = p.get
    if(u.isEmpty){
      response.state = 1
      server.sendPacket(response)
      return
    }
    val s = SessionManager.getSession(u.get, this.password)
    if(s.isEmpty){
      response.state = 2
      server.sendPacket(response)
      u.get.onFailedAuthAttempt(p.get.ip, this.password, "nailed-forge")
      return
    }
    response.state = 0
    server.sendPacket(response)
    server.sendPacket(new PacketUserdata(u.get))
    p.get.user = u.get
    p.get.session = s.get
  }
}
