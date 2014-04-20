package jk_5.nailed.web.game

import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.auth.{AuthSession, User}

/**
 * No description given
 *
 * @author jk-5
 */
case class Player(id: String, name: String, ip: String){
  var user: User = _
  var session: AuthSession = _

  def toJson = new JsonObject().add("id", this.id).add("name", this.name)
}
