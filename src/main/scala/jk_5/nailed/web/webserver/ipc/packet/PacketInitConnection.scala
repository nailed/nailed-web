package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.webserver.ipc.PacketUtils
import jk_5.nailed.web.game.Player
import jk_5.nailed.web.mappack.Mappack
import scala.collection.mutable

/**
 * No description given
 *
 * @author jk-5
 */
class PacketInitConnection extends IpcPacket {

  private var players: mutable.ArrayBuffer[Player] = _
  private var mappacks: mutable.ArrayBuffer[Mappack] = _

  override def encode(buffer: ByteBuf){}
  override def decode(buffer: ByteBuf){
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
      println(id + " - " + name + " - " + lobby + " - " + hidden + " - " + iconLength)
    }
  }

  override def processPacket(){

  }
}
