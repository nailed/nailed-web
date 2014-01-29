package jk_5.nailed.web.webserver.packet

import jk_5.nailed.web.webserver.NetworkHandler
import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
class PacketAuthResponse(var success: Boolean = false, var userData: JsonObject = null) extends Packet {

  def write(data: JsonObject){
    data.set("success", this.success)
    if(this.userData != null) data.set("userData", this.userData)
  }

  def read(data: JsonObject){

  }

  def processPacket(handler: NetworkHandler){

  }
}
