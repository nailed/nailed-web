package jk_5.nailed.web.mappack

import jk_5.nailed.web.game.GameServer
import org.apache.logging.log4j.LogManager
import scala.collection.mutable

/**
 * No description given
 *
 * @author jk-5
 */
object MappackRegistry {

  private val logger = LogManager.getLogger
  val mappacks = mutable.ArrayBuffer[Mappack]()

  def getById(id: String): Option[Mappack] = this.mappacks.find(_.id == id)

  def addServerMappacks(server: GameServer) = this.mappacks synchronized {
    this.mappacks.appendAll(server.mappacks)
  }

  def removeServerMappacks(server: GameServer) = this.mappacks synchronized {
    server.mappacks.foreach(m => this.mappacks.remove(this.mappacks.indexOf(m)))
  }
}
