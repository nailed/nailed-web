package jk_5.nailed.web.auth

import jk_5.jsonlibrary.JsonObject
import java.util.Date
import jk_5.nailed.web.couchdb.{TCouchDBSerializable, UID, DatabaseType}

/**
 * No description given
 *
 * @author jk-5
 */
@DatabaseType("session")
class AuthSession(private var userID: UID) extends TCouchDBSerializable {

  private var created = new Date

  protected def writeToJsonForDB(data: JsonObject){
    data.add("userID", this.userID.toString)
    data.add("created", this.created.getTime)
  }
  protected def readFromJsonForDB(data: JsonObject){
    this.userID = new UID(data.get("userID").asString)
    this.created = new Date(data.get("created").asLong)
  }

  def toJson = new JsonObject().add("id", this.getID.toString).add("created", this.created.getTime)

  @inline def getUser = UserDatabase.getUser(this.userID)
}
