package jk_5.nailed.web.game

import scala.collection.mutable
import jk_5.nailed.web.mappack.Mappack
import io.netty.channel.Channel
import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
class GameServer(private val channel: Channel, private val players: mutable.ArrayBuffer[Player], private val mappacks: mutable.ArrayBuffer[Mappack]) {

  var address: String = _

  def getChannel = this.channel

  def toJson = new JsonObject().add("address", this.address)
}
