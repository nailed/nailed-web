package jk_5.nailed.web.webserver.ipc.packet

import jk_5.nailed.web.game.GameServer
import io.netty.buffer.ByteBuf
import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.webserver.ipc.PacketUtils

/**
 * No description given
 *
 * @author jk-5
 */
class PacketLoadMappackMeta extends IpcPacket {

  var id: String = _
  var data: JsonObject = _

  override def encode(buffer: ByteBuf){
    PacketUtils.writeString(id, buffer)
    PacketUtils.writeString(data.stringify, buffer)
  }

  override def decode(buffer: ByteBuf){

  }

  override def processPacket(server: GameServer){

  }
}
