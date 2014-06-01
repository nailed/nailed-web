package jk_5.nailed.web.game

import scala.collection.mutable
import io.netty.channel.Channel
import jk_5.jsonlibrary.{JsonArray, JsonObject}
import jk_5.nailed.web.webserver.ipc.ProtocolIpc
import jk_5.nailed.web.chat.ChatHandler

/**
 * No description given
 *
 * @author jk-5
 */
case class GameServer(private val channel: Channel) {

  var address: String = _
  private val players = mutable.ArrayBuffer[Player]()

  def toJson: JsonObject = {
    val obj = new JsonObject().add("address", this.address)
    val playerlist = new JsonArray
    this.players.foreach(p => playerlist.add(p.toJson))
    obj.add("players", playerlist)
    obj
  }

  def onPlayerJoin(player: Player){
    ProtocolIpc.logger.info(ProtocolIpc.marker, s"Player ${player.name} joined!")
    this.players += player
  }

  def onPlayerLeave(player: Player){
    ProtocolIpc.logger.info(ProtocolIpc.marker, s"Player ${player.name} left!")
    this.players -= player
    ChatHandler.onPlayerLeft(player)
  }

  @inline def getPlayer(id: String) = this.players.find(_.id == id)
  @inline def getChannel = this.channel
  @inline def sendPacket(msg: Any) = this.channel.writeAndFlush(msg)
}
