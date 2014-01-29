package jk_5.nailed.web.webserver.packet

import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.webserver._
import jk_5.nailed.web.auth.{UserDatabase, SessionManager}

/**
 * No description given
 *
 * @author jk-5
 */
class PacketAuthenticate(var email: String = null, var password: String = null, var connectionType: ConnectionType.ConnectionType = null) extends Packet{
  def this() = this(null, null, null)

  def write(data: JsonObject){
    data.set("email", this.email).set("password", this.password).set("connectionType", this.connectionType.toString.toLowerCase)
  }

  def read(data: JsonObject){
    this.email = data.get("email").asString
    this.password = data.get("password").asString
    this.connectionType = ConnectionType.withName(data.get("type").asString.toUpperCase)
  }

  def processPacket(handler: NetworkHandler){
    if(!handler.getConnection.isAuthenticated){
      var user = UserDatabase.getUser(this.email)
      if(user.isEmpty){
        user = Some(UserDatabase.createUser(this.email, this.password))
      }
      val session = SessionManager.getSession(user.get, this.password)
      if(session.isEmpty){
        handler.sendPacket(new PacketAuthResponse(false))
        return
      }
      if(this.connectionType == ConnectionType.CLIENT){
        handler.setConnection(new ClientConnection(handler))
      }else if(this.connectionType == ConnectionType.SERVER){
        handler.setConnection(new ServerConnection(handler))
      }else return
      handler.getConnection.authenticatedUser = user.get
      handler.sendPacket(new PacketAuthResponse(true, session.get.getUser.get.getUserInfo))
    }else handler.sendPacket(new PacketAuthResponse(false))
  }
}
