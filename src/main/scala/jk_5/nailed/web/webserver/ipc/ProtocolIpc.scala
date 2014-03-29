package jk_5.nailed.web.webserver.ipc

import jk_5.nailed.web.webserver.MultiplexedProtocol
import io.netty.channel.ChannelPipeline
import jk_5.nailed.web.webserver.ipc.codec.{PacketCodec, VarintFrameCodec}
import io.netty.util.AttributeKey

/**
 * No description given
 *
 * @author jk-5
 */
object ProtocolIpc extends MultiplexedProtocol {
  val gameServer = AttributeKey.valueOf("gameServer")

  override def matches(byte1: Int, byte2: Int): Boolean = byte1 == 186 && byte2 == 96
  override def configurePipeline(pipe: ChannelPipeline){
    pipe.addLast("framer", new VarintFrameCodec)
    pipe.addLast("packetCodec", new PacketCodec)
    pipe.addLast("handler", SimplePacketHandler)
  }
}
