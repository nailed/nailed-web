package jk_5.nailed.web.chat

import jk_5.nailed.web.game.Player
import jk_5.nailed.web.webserver.irc.{ProtocolIrc, PlayerConnection}

/**
 * Manages the bridge chat between the gameserver and irc and other potential chat protocols
 *
 * All chat is redirected to #global on irc. Messages sent to here will be available to every minecraft player
 * For each map, a seperate channel is created on irc. The names are in the format of #map_mappack_id (Example: #map_3_nail)
 * Each team in a map, has a seperate channel too. These names will be in the format of #mappack_id_teamid
 * Also, private messages to players will be sent to the gameserver and to irc, but only to the players talking to each other
 *
 * @author jk-5
 */
object ChatHandler {

  lazy val globalChatChannel = ProtocolIrc.getOrCreateChannel("#global")

  def onPlayerLeft(player: Player){
    player.chatConnection.foreach(conn => {
      conn.disconnected("Left the game")
    })
  }

  def onPlayerAuthenticated(player: Player){
    val conn = new PlayerConnection(player)
    player.chatConnection = Some(conn)
    conn.connected()

    conn join globalChatChannel
  }

  def sendGlobalChat(player: Player, message: String){
    player.chatConnection.foreach(conn => {
      conn.sendMessage(this.globalChatChannel, message)
    })
  }
}
