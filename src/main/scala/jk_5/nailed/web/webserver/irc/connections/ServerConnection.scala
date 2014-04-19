package jk_5.nailed.web.webserver.irc.connections

import jk_5.nailed.web.webserver.irc.{ProtocolIrc, IrcConnection}

/**
 * No description given
 *
 * @author jk-5
 */
object ServerConnection extends IrcConnection {

  this.setAllNames(ProtocolIrc.host)

  override def onPrivateMessage(connection: IrcConnection, message: String){
    val command = message.substring(0, message.indexOf(' ')).toLowerCase
    val args = message.substring(message.indexOf(' ') + 1)
    command match {
      case "say" =>
        val parts = args.split(" ", 2)
        this.sendMessage(ProtocolIrc.getChannel(parts(0)).get, parts(1))
    }
  }

  override def noJoinNeeded = true
}
