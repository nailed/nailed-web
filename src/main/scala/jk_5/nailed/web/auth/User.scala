package jk_5.nailed.web.auth

import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.couchdb.{TCouchDBSerializable, DatabaseType}
import jk_5.nailed.web.auth.mojang.AuthData

/**
 * No description given
 *
 * @author jk-5
 */
@DatabaseType("user")
case class User(private var username: String) extends TCouchDBSerializable {

  private var passwordHash: String = _
  private var fullName: String = _
  private var email: String = _
  private val authData = new AuthData

  def writeToJsonForDB(data: JsonObject){
    data.add("username", this.username)
    data.add("email", this.email)
    data.add("passwordHash", this.passwordHash)
    data.add("fullName", this.fullName)
    data.add("authData", this.authData.toJson)
  }

  def readFromJsonForDB(data: JsonObject){
    this.email = data.get("email").asString
    this.passwordHash = data.get("passwordHash").asString
    this.fullName = data.get("fullName").asString
    if(data.get("authData") != null) this.authData.read(data.get("authData").asObject)
    if(data.get("username") != null) this.username = data.get("username").asString
  }

  def getUserInfo: JsonObject = {
    val obj = new JsonObject
    obj.set("username", this.username)
    obj.set("email", this.email)
    obj.set("fullName", this.fullName)
  }

  @inline def getUsername = this.username
  @inline def getEmail = this.email
  @inline def getPasswordHash = this.passwordHash
  @inline def getFullName = this.fullName
  @inline def getAuthData = this.authData
  @inline def setPasswordHash(hash: String) = this.passwordHash = hash
  @inline def setFullName(name: String) = this.fullName = name
  @inline def setEmail(email: String) = this.email = email
}
