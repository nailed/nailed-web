package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.webserver.ipc.{ProtocolIpc, PacketUtils}
import jk_5.nailed.web.game.{ServerRegistry, GameServer, Player}
import jk_5.nailed.web.mappack.Mappack
import scala.collection.mutable

/**
 * No description given
 *
 * @author jk-5
 */
class PacketInitConnection extends IpcPacket {

  private var host: String = _
  private var players: mutable.ArrayBuffer[Player] = _
  private var mappacks: mutable.ArrayBuffer[Mappack] = _

  override def encode(buffer: ByteBuf){}
  override def decode(buffer: ByteBuf){
    this.host = PacketUtils.readString(buffer)
    val pCount = PacketUtils.readVarInt(buffer)
    for(i <- 0 until pCount){
      val p = new Player
      val playerId = PacketUtils.readString(buffer)
      val playerName = PacketUtils.readString(buffer)
      this.players += p
    }
    val mCount = PacketUtils.readVarInt(buffer)
    for(i <- 0 until mCount){
      val id = PacketUtils.readString(buffer)
      val name = PacketUtils.readString(buffer)
      val lobby = buffer.readBoolean()
      val hidden = buffer.readBoolean()
      val iconLength = PacketUtils.readVarInt(buffer)
      val icon = buffer.readBytes(iconLength)
    }
  }

  override def processPacket(server: GameServer){
    ProtocolIpc.logger.trace(ProtocolIpc.connectionMarker, "IPC Connection opened")
    val srv = new GameServer(server.getChannel, this.players, this.mappacks)
    server.getChannel.attr(ProtocolIpc.gameServer).set(srv)
    ServerRegistry.addServer(srv)
  }
}
