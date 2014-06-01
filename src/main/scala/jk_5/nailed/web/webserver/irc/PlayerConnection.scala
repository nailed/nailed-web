package jk_5.nailed.web.webserver.irc

import jk_5.nailed.web.game.Player
import jk_5.nailed.web.webserver.ipc.packet.PacketChat

/**
 * No description given
 *
 * @author jk-5
 */
class PlayerConnection(val player: Player) extends IrcConnection with AuthenticatedConnection {

  this.hostname = player.ip
  this.nickname = player.user.username
  this.login = player.user.username
  this.realname = player.user.fullName

  override def getUser = player.user
  override def getSession = player.session

  override def onChannelMessage(sender: IrcConnection, channel: IrcChannel, message: String){
    if(channel.name == "#global"){
      val packet = new PacketChat
      packet.destId = this.player.id
      packet.message = s"<${sender.modePrefix(channel)}${sender.nickname}> $message"
      player.server.sendPacket(packet)
    }
  }
}
