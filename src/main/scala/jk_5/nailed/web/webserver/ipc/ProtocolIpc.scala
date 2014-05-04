package jk_5.nailed.web.webserver.ipc

import jk_5.nailed.web.webserver.MultiplexedProtocol
import io.netty.channel.Channel
import jk_5.nailed.web.webserver.ipc.codec.{PacketCodec, VarintFrameCodec}
import io.netty.util.AttributeKey
import org.apache.logging.log4j.{MarkerManager, LogManager}
import jk_5.nailed.web.game.GameServer
import io.netty.buffer.ByteBuf

/**
 * No description given
 *
 * @author jk-5
 */
object ProtocolIpc extends MultiplexedProtocol {
  val logger = LogManager.getLogger
  val marker = MarkerManager.getMarker("IPC")
  val connectionMarker = MarkerManager.getMarker("IPC|Connections", this.marker)
  val gameServer: AttributeKey[GameServer] = AttributeKey.valueOf("gameServer")

  override def matches(buffer: ByteBuf): Boolean = {
    val byte1 = buffer.getUnsignedByte(buffer.readerIndex())
    val byte2 = buffer.getUnsignedByte(buffer.readerIndex() + 1)
    byte1 == 1 && byte2 == 0
  }
  override def configureChannel(channel: Channel){
    this.logger.trace(this.connectionMarker, "Incoming IPC connection from {}", channel.remoteAddress().toString)
    val pipe = channel.pipeline()
    pipe.addLast("framer", new VarintFrameCodec)
    pipe.addLast("packetCodec", new PacketCodec)
    pipe.addLast("handler", SimplePacketHandler)
  }
}
