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
class PacketCreateAccount extends IpcPacket {

  var playerid: String = _
  var username: String = _
  var email: String = _
  var name: String = _
  var password: String = _

  override def encode(buffer: ByteBuf){}
  override def decode(buffer: ByteBuf){
    this.playerid = PacketUtils.readString(buffer)
    this.username = PacketUtils.readString(buffer)
    this.email = PacketUtils.readString(buffer)
    this.name = PacketUtils.readString(buffer)
    this.password = PacketUtils.readString(buffer)
  }

  override def processPacket(server: GameServer){
    val p = server.getPlayer(playerid)
    if(p.isEmpty){
      //TODO: send error
      return
    }
    if(UserDatabase.getUserByUsername(username).isDefined){
      //TODO: send error
      return
    }
    if(UserDatabase.getUserByEmail(email).isDefined){
      //TODO: send error
      return
    }
    val user = UserDatabase.createUser(username, email, password, name)
    val session = SessionManager.getSession(user, password)
    p.get.user = user
    p.get.session = session.get
    val res = new PacketLoginResponse
    res.player = p.get
    res.state = 0
    server.sendPacket(res)
  }
}
