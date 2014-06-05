package jk_5.nailed.web.webserver.ipc.packet

import jk_5.nailed.web.game.GameServer
import io.netty.buffer.ByteBuf
import jk_5.nailed.web.webserver.ipc.PacketUtils
import jk_5.nailed.web.mappack.MappackRegistry

/**
 * No description given
 *
 * @author jk-5
 */
class PacketRequestMappackLoad extends IpcPacket {

  var name: String = _
  var autoload = true

  override def encode(buffer: ByteBuf){

  }

  override def decode(buffer: ByteBuf){
    this.name = PacketUtils.readString(buffer)
    this.autoload = buffer.readBoolean()
  }

  override def processPacket(server: GameServer){
    val mappack = MappackRegistry.getById(this.name)
    if(mappack.isEmpty) return
    mappack.get.load(server, autoload)
  }
}
