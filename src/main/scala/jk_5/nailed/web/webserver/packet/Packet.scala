package jk_5.nailed.web.webserver.packet

import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.webserver.{PacketManager, NetworkHandler}

/**
 * No description given
 *
 * @author jk-5
 */
abstract class Packet {
  private var decoder: String = ""
  def processPacket(handler: NetworkHandler)
  def write(data: JsonObject)
  def read(data: JsonObject)
  def hasData = true
  final def getPacketID = PacketManager.getPacketID(this.getClass)
  final def setDecoder(decoder: String) = this.decoder = decoder
  final def getDecoder = this.decoder
}
