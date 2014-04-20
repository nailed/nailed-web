package jk_5.nailed.web.webserver.irc

import jk_5.nailed.web.auth.{AuthSession, User}

/**
 * No description given
 *
 * @author jk-5
 */
class PlayerConnection extends IrcConnection with AuthenticatedConnection {
  override def getUser: User = ???
  override def getSession: AuthSession = ???
}
