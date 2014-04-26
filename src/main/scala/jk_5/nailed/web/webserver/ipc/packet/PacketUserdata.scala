package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.ByteBuf
import jk_5.nailed.web.game.GameServer
import jk_5.nailed.web.auth.User
import jk_5.nailed.web.webserver.ipc.PacketUtils

/**
 * No description given
 *
 * @author jk-5
 */
class PacketUserdata(var user: User = null) extends IpcPacket {
  def this(){this(null)}

  override def encode(buffer: ByteBuf){
    PacketUtils.writeString(user.getID.toString, buffer)
    PacketUtils.writeString(user.username, buffer)
    PacketUtils.writeString(user.fullName, buffer)
    PacketUtils.writeString(user.email, buffer)
  }

  override def decode(buffer: ByteBuf){}
  override def processPacket(server: GameServer){}
}
