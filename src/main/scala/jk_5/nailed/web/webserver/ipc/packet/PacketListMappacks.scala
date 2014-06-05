package jk_5.nailed.web.webserver.ipc.packet

import jk_5.nailed.web.game.GameServer
import io.netty.buffer.ByteBuf
import jk_5.nailed.web.mappack.MappackRegistry
import jk_5.nailed.web.webserver.ipc.PacketUtils

/**
 * No description given
 *
 * @author jk-5
 */
class PacketListMappacks extends IpcPacket {

  override def encode(buffer: ByteBuf){
    PacketUtils.writeVarInt(MappackRegistry.mappacks.size, buffer)
    MappackRegistry.mappacks.foreach(m => PacketUtils.writeString(m.mpid, buffer))
  }

  override def decode(buffer: ByteBuf){

  }

  override def processPacket(server: GameServer){
    server.sendPacket(new PacketListMappacks)
  }
}
