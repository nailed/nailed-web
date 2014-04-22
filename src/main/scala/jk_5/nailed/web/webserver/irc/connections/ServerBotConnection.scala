package jk_5.nailed.web.webserver.irc.connections

import jk_5.nailed.web.webserver.irc.{ProtocolIrc, IrcConnection}

/**
 * No description given
 *
 * @author jk-5
 */
object ServerBotConnection extends IrcConnection("*server") {

  override def onPrivateMessage(connection: IrcConnection, message: String){
    val command = message.substring(0, message.indexOf(' ')).toLowerCase
    val args = message.substring(message.indexOf(' ') + 1)
    command match {
      case "join" => this.join(ProtocolIrc.getChannel(args).get)
      case "part" => this.part(ProtocolIrc.getChannel(args).get)
      case "say" =>
        val parts = args.split(" ", 2)
        this.sendMessage(ProtocolIrc.getChannel(parts(0)).get, parts(1))
    }
  }
}
