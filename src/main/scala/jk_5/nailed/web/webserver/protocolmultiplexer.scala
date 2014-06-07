package jk_5.nailed.web.webserver

import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.channel.{Channel, ChannelHandlerContext}
import io.netty.buffer.ByteBuf
import java.util
import scala.collection.mutable.ArrayBuffer

/**
 * No description given
 *
 * @author jk-5
 */
object ProtocolMultiplexer {
  private final val handlers = ArrayBuffer[ServerProtocol]()
  def addHandler(handler: ServerProtocol) = this.handlers += handler
}

class ProtocolMultiplexer extends ByteToMessageDecoder {

  def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]){
    if(in.readableBytes() < 2) return
    val handler = ProtocolMultiplexer.handlers.find(_.matches(in))
    if(handler.isEmpty){
      in.clear()
      ctx.close()
      return
    }
    handler.get.configureChannel(ctx.channel())
    ctx.pipeline().remove(this)
  }
}

trait ServerProtocol{
  def matches(buffer: ByteBuf): Boolean
  def configureChannel(channel: Channel)
}
