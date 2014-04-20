package jk_5.nailed.web.game

import scala.collection.mutable
import jk_5.nailed.web.mappack.Mappack
import io.netty.channel.Channel
import jk_5.jsonlibrary.{JsonArray, JsonObject}
import jk_5.nailed.web.webserver.ipc.ProtocolIpc

/**
 * No description given
 *
 * @author jk-5
 */
class GameServer(private val channel: Channel, private val players: mutable.ArrayBuffer[Player], private val mappacks: mutable.ArrayBuffer[Mappack]) {

  var address: String = _

  def toJson: JsonObject = {
    val obj = new JsonObject().add("address", this.address)
    val playerlist = new JsonArray
    this.players.foreach(p => playerlist.add(p.toJson))
    obj.add("players", playerlist)
    val mappacklist = new JsonArray
    this.mappacks.foreach(m => mappacklist.add(m.toJson))
    obj.add("mappacks", mappacklist)
    obj
  }

  def onPlayerJoin(player: Player){
    ProtocolIpc.logger.info(ProtocolIpc.marker, s"Player ${player.name} joined!")
    this.players += player

    println(s"New player list size: ${this.players.size}")
  }

  def onPlayerLeave(player: Player){
    ProtocolIpc.logger.info(ProtocolIpc.marker, s"Player ${player.name} left!")
    this.players -= player

    println(s"New player list size: ${this.players.size}")
  }

  @inline def getPlayer(id: String) = this.players.find(_.id == id)
  @inline def getChannel = this.channel
  @inline def sendPacket(msg: Any) = this.channel.writeAndFlush(msg)
}
