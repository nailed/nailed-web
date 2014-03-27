package jk_5.nailed.web.webserver.ipc

import jk_5.nailed.web.webserver.MultiplexedProtocol
import io.netty.channel.ChannelPipeline

/**
 * No description given
 *
 * @author jk-5
 */
object ProtocolIpc extends MultiplexedProtocol {
  override def matches(byte1: Int, byte2: Int): Boolean = byte1 == 0xA && byte2 == 0xB
  override def configurePipeline(pipe: ChannelPipeline){
    pipe.addLast("varintFramer", new VarintFrameCodec)
  }
}
