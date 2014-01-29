package jk_5.nailed.web.webserver

import jk_5.nailed.web.auth.User

/**
 * No description given
 *
 * @author jk-5
 */
abstract class Connection(val connectionType: ConnectionType.ConnectionType, var networkHandler: NetworkHandler = null) {
  var authenticatedUser: User = null

  def isAuthenticated = this.authenticatedUser != null
}

class ClientConnection(networkHandler: NetworkHandler = null) extends Connection(ConnectionType.CLIENT, networkHandler){

}

class ServerConnection(networkHandler: NetworkHandler = null) extends Connection(ConnectionType.SERVER, networkHandler){
  override def isAuthenticated = true
}

class NullConnection extends Connection(null){
  override def isAuthenticated = false
}
