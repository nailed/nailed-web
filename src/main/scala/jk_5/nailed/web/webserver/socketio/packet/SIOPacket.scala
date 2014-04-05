package jk_5.nailed.web.webserver.socketio.packet

/**
 * No description given
 *
 * @author jk-5
 */
abstract class SIOPacket(private val typ: PacketType) {

  var id = Long.MinValue
  var endpoint: String = _
  var ack: Any = _


  def getType = this.typ
}
