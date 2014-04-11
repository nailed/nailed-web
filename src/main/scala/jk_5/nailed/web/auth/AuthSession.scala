package jk_5.nailed.web.auth

import jk_5.jsonlibrary.JsonObject
import java.util.Date
import jk_5.nailed.web.couchdb.UID

/**
 * No description given
 *
 * @author jk-5
 */
class AuthSession(private var userID: UID) {

  private val id: UID = UID.randomUID
  private val created = new Date
  private var user: Option[User] = None

  def toJson = new JsonObject().add("id", this.id.toString).add("created", this.created.getTime)

  @inline def getUser: User = {
    if(this.user.isDefined) this.user.get
    else{
      this.user = UserDatabase.getUser(this.userID)
      this.user.get
    }
  }
  @inline def getID = this.id
  @inline def getUserID = this.userID
}
