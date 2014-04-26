package jk_5.nailed.web.game

import scala.collection.mutable
import org.apache.logging.log4j.LogManager
import jk_5.nailed.web.mappack.MappackRegistry

/**
 * No description given
 *
 * @author jk-5
 */
object ServerRegistry {

  private val logger = LogManager.getLogger
  private val servers = mutable.ArrayBuffer[GameServer]()

  def addServer(server: GameServer){
    this.logger.info("Gameserver {} connected!", server.getChannel.remoteAddress())
    this.servers += server
    MappackRegistry.addServerMappacks(server)
  }

  def removeServer(server: GameServer){
    this.logger.info("Gameserver {} disconnected!", server.getChannel.remoteAddress())
    this.servers -= server
    MappackRegistry.removeServerMappacks(server)
  }

  def getServers = this.servers
}
