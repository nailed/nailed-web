package jk_5.nailed.web.auth

import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.couchdb.{TCouchDBSerializable, DatabaseType}
import jk_5.nailed.web.auth.mojang.AuthData
import jk_5.nailed.web.webserver.irc.{AuthenticatedConnection, ProtocolIrc}
import jk_5.nailed.web.webserver.irc.connections.ServerBotConnection

/**
 * No description given
 *
 * @author jk-5
 */
@DatabaseType("user")
case class User(var username: String) extends TCouchDBSerializable {

  var passwordHash: String = _
  var fullName: String = _
  var email: String = _
  val authData = new AuthData
  val permissions = new PermissionContainer
  var activated = false

  def writeToJsonForDB(data: JsonObject){
    data.add("username", this.username)
    data.add("email", this.email)
    data.add("passwordHash", this.passwordHash)
    data.add("fullName", this.fullName)
    data.add("authData", this.authData.toJson)
    data.add("permissions", this.permissions.toJson)
    data.add("activated", this.activated)
  }

  def readFromJsonForDB(data: JsonObject){
    this.email = data.get("email").asString
    this.passwordHash = data.get("passwordHash").asString
    this.fullName = data.get("fullName").asString
    if(data.get("authData") != null) this.authData.read(data.get("authData").asObject)
    if(data.get("username") != null) this.username = data.get("username").asString
    if(data.get("permissions") != null) this.permissions.read(data.get("permissions").asObject)
    if(data.get("activated") != null) this.activated = data.get("activated").asBoolean
  }

  def getUserInfo: JsonObject = {
    val obj = new JsonObject
    obj.set("username", this.username)
    obj.set("email", this.email)
    obj.set("fullName", this.fullName)
    obj.set("permissions", this.permissions.toJson)
  }

  def onFailedAuthAttempt(ip: String, pass: String, origin: String){
    ProtocolIrc.connections
      .filter(_.isInstanceOf[AuthenticatedConnection])
      .map(_.asInstanceOf[AuthenticatedConnection])
      .filter(_.getUser == this)
      .foreach(c => {
        ServerBotConnection.sendMessage(c, "Someone tried to login to your account but gave the wrong password")
        ServerBotConnection.sendMessage(c, s"IP: $ip Password: $pass Origin: $origin")
      })
  }
}
