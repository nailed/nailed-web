package jk_5.nailed.web.webserver.irc

import jk_5.nailed.web.auth.{AuthSession, User}

/**
 * No description given
 *
 * @author jk-5
 */
trait AuthenticatedConnection extends IrcConnection {
  def getUser: User
  def getSession: AuthSession
}
