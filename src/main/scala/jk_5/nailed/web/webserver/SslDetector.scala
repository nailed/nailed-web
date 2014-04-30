package jk_5.nailed.web.webserver

import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.channel.{Channel, ChannelHandlerContext}
import io.netty.buffer.ByteBuf
import java.util
import io.netty.handler.ssl.SslHandler
import io.netty.util.AttributeKey

/**
 * No description given
 *
 * @author jk-5
 */
object SslDetector {
  val ssl = AttributeKey.valueOf[Boolean]("isSsl")
  def isSsl(channel: Channel): Boolean = Option(channel.attr(ssl).get()).getOrElse(false)
}

class SslDetector extends ByteToMessageDecoder {
  override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]){
    if(in.readableBytes() < 5) return
    val isSsl = SslHandler.isEncrypted(in)
    if(isSsl && SslContextProvider.isValid){
      val engine = SslContextProvider.getContext.createSSLEngine()
      engine.setUseClientMode(false)
      ctx.pipeline().addAfter(ctx.name(), "ssl", new SslHandler(engine))
      ctx.channel().attr(SslDetector.ssl).set(true)
    }
    //Because we don't actually read anything from the buffer, this will cause the handler to pass on the buffer to
    //The next handler in the pipeline, so we don't have to do that ourselves
    ctx.pipeline().remove(this)
  }
}
