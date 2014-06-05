package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.webserver.ipc.{ProtocolIpc, PacketUtils}
import jk_5.nailed.web.game.{ServerRegistry, GameServer, Player}
import scala.collection.mutable

/**
 * No description given
 *
 * @author jk-5
 */
class PacketInitConnection extends IpcPacket {

  private var host: String = _
  private val players = mutable.ArrayBuffer[(String, String, String)]()

  override def encode(buffer: ByteBuf){}
  override def decode(buffer: ByteBuf){
    this.host = PacketUtils.readString(buffer)
    val pCount = PacketUtils.readVarInt(buffer)
    for(i <- 0 until pCount){
      this.players += ((PacketUtils.readString(buffer), PacketUtils.readString(buffer), "unknown")) //FIXME: replace "unknown" with proper ip
    }
  }

  override def processPacket(server: GameServer){
    ProtocolIpc.logger.trace(ProtocolIpc.connectionMarker, "IPC Connection opened")
    val srv = new GameServer(server.getChannel)
    this.players.foreach(p => srv.onPlayerJoin(new Player(p._1, p._2, p._3, srv)))
    srv.address = this.host
    server.getChannel.attr(ProtocolIpc.gameServer).set(srv)
    ServerRegistry.addServer(srv)

    srv.sendPacket(new PacketListMappacks)
  }
}
