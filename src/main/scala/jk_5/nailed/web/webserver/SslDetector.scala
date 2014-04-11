package jk_5.nailed.web.webserver

import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.channel.{Channel, ChannelHandlerContext}
import io.netty.buffer.ByteBuf
import java.util
import io.netty.handler.ssl.SslHandler

/**
 * No description given
 *
 * @author jk-5
 */
object SslDetector {
  def isSsl(channel: Channel): Boolean = channel.pipeline().names().contains("ssl")
}

class SslDetector extends ByteToMessageDecoder {
  override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]){
    if(in.readableBytes() < 5) return
    val isSsl = SslHandler.isEncrypted(in)
    if(isSsl && SslContextProvider.isValid){
      val engine = SslContextProvider.getContext.createSSLEngine()
      engine.setUseClientMode(false)
      ctx.pipeline().addAfter(ctx.name(), "ssl", new SslHandler(engine))
    }
    ctx.pipeline().remove(this)
  }
}
