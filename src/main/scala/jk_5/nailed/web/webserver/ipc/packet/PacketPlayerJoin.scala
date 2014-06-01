package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.game.{Player, GameServer}
import jk_5.nailed.web.webserver.ipc.PacketUtils

/**
 * No description given
 *
 * @author jk-5
 */
class PacketPlayerJoin extends IpcPacket {

  var data: (String, String, String) = _

  override def encode(buffer: ByteBuf){}
  override def decode(buffer: ByteBuf){
    this.data = (PacketUtils.readString(buffer), PacketUtils.readString(buffer), PacketUtils.readString(buffer))
  }
  override def processPacket(server: GameServer){
    val player = new Player(data._1, data._2, data._3, server)
    server.onPlayerJoin(player)

    val p = new PacketPromptLogin
    p.player = player
    server.sendPacket(p)
  }
}
