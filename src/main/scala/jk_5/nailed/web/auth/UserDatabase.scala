package jk_5.nailed.web.auth

import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.couchdb.{UID, CouchDB}
import jk_5.nailed.web.NailedWeb

/**
 * No description given
 *
 * @author jk-5
 */
object UserDatabase {

  def getUserByEmail(email: String): Option[User] = {
    val data = CouchDB.getViewData("users", "byEmail").get()
    val json = JsonObject.readFrom(data.getResponseBody).get("rows").asArray
    val userData = json.getValues.map(_.asObject).find(_.get("key").asString == email).map(_.get("value").asObject)
    if(userData.isDefined){
      val u = new User(userData.get.get("username").asString)
      u.readDB(userData.get)
      Some(u)
    } else None
  }

  def getUserByUsername(username: String): Option[User] = {
    val data = CouchDB.getViewData("users", "byUsername").get()
    val json = JsonObject.readFrom(data.getResponseBody).get("rows").asArray
    val userData = json.getValues.map(_.asObject).find(_.get("key").asString == username).map(_.get("value").asObject)
    if(userData.isDefined){
      val u = new User(username)
      u.readDB(userData.get)
      Some(u)
    } else None
  }

  def getUser(id: UID): Option[User] = {
    val idString = id.toString
    val data = CouchDB.getViewData("users", "byId").get()
    val json = JsonObject.readFrom(data.getResponseBody).get("rows").asArray
    val userData = json.getValues.map(_.asObject).find(_.get("key").asString == idString).map(_.get("value").asObject)
    if(userData.isDefined){
      val u = new User(userData.get.get("username").asString)
      u.readDB(userData.get)
      Some(u)
    } else None
  }

  def createUser(username: String, email: String, password: String): User = this.createUser(username, email, password, username)
  def createUser(username: String, email: String, password: String, fullName: String): User = {
    val future = NailedWeb.worker.submit(new CreateSCryptHashTask(password))
    val user = new User(username)
    user.email = email
    user.fullName = fullName
    user.passwordHash = future.get()
    user.saveToDatabase()
    user
  }
}
