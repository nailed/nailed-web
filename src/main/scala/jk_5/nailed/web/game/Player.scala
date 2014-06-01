package jk_5.nailed.web.game

import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.auth.{AuthSession, User}
import jk_5.nailed.web.webserver.irc.PlayerConnection

/**
 * No description given
 *
 * @author jk-5
 */
case class Player(id: String, name: String, ip: String, server: GameServer){
  var user: User = _
  var session: AuthSession = _
  var chatConnection: Option[PlayerConnection] = None

  def toJson = new JsonObject().add("id", this.id).add("name", this.name)
}
