package jk_5.nailed.web.auth

import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.couchdb.{TCouchDBSerializable, DatabaseType}

/**
 * No description given
 *
 * @author jk-5
 */
@DatabaseType("user")
case class User(private var email: String) extends TCouchDBSerializable {

  private var passwordHash: String = _
  private var fullName: String = _

  def writeToJsonForDB(data: JsonObject){
    data.add("email", this.email)
    data.add("passwordHash", this.passwordHash)
    data.add("fullName", this.fullName)
  }

  def readFromJsonForDB(data: JsonObject){
    this.email = data.get("email").asString
    this.passwordHash = data.get("passwordHash").asString
    this.fullName = data.get("fullName").asString
  }

  def getUserInfo: JsonObject = {
    val obj = new JsonObject
    obj.set("email", this.email)
    obj.set("fullName", this.fullName)
  }

  @inline def getEmail = this.email
  @inline def getPasswordHash = this.passwordHash
  @inline def getFullName = this.fullName
  @inline def setPasswordHash(hash: String) = this.passwordHash = hash
  @inline def setFullName(name: String) = this.fullName = name
}
