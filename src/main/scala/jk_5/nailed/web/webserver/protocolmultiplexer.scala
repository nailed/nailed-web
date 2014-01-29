package jk_5.nailed.web.webserver

import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.channel.{ChannelPipeline, ChannelHandlerContext}
import io.netty.buffer.ByteBuf
import java.util
import io.netty.handler.timeout.ReadTimeoutHandler
import scala.collection.mutable.ArrayBuffer

/**
 * No description given
 *
 * @author jk-5
 */
object ProtocolMultiplexer {
  private final val handlers = ArrayBuffer[MultiplexedProtocol]()
  def addHandler(handler: MultiplexedProtocol) = this.handlers += handler
}

class ProtocolMultiplexer extends ByteToMessageDecoder {

  def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]){
    if(in.readableBytes() < 2) return
    val byte1 = in.getUnsignedByte(in.readerIndex())
    val byte2 = in.getUnsignedByte(in.readerIndex() + 1)
    val handler = ProtocolMultiplexer.handlers.find(_.matches(byte1, byte2))
    if(handler.isEmpty){
      in.clear()
      ctx.close()
      return
    }
    ctx.pipeline().remove(ReadTimeoutDetector.getClass)
    ctx.pipeline().remove(classOf[ReadTimeoutHandler])
    handler.get.configurePipeline(ctx.pipeline())
    ctx.pipeline().remove(this)
  }
}

trait MultiplexedProtocol{
  def matches(byte1: Int, byte2: Int): Boolean
  def configurePipeline(pipeline: ChannelPipeline)
}
