package jk_5.nailed.web.webserver.packet

import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.webserver.NetworkHandler

/**
 * No description given
 *
 * @author jk-5
 */
class PacketCloseConnection(var reason: String = null) extends Packet {

  def write(data: JsonObject){
    data.add("reason", this.reason)
  }
  def read(data: JsonObject){
    this.reason = data.get("reason").asString
  }
  def processPacket(handler: NetworkHandler){
    handler.closeConnection(this.reason)
  }
}
