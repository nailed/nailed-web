package jk_5.nailed.web.webserver.http.handlers

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.{HttpMethod, FullHttpRequest}
import jk_5.nailed.web.webserver.RoutedHandler
import io.netty.handler.codec.http.multipart._
import jk_5.nailed.web.webserver.http.WebServerUtils
import java.io.File

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerUpload extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {

  val factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE)

  override def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    if(msg.getMethod == HttpMethod.POST){
      val decoder = new HttpPostRequestDecoder(this.factory, msg)
      val file = decoder.getBodyHttpData("file1").asInstanceOf[FileUpload]
      file.renameTo(new File(s"uploaded/${file.getFilename}"))
      WebServerUtils.sendOK(ctx)
      decoder.destroy()

      this.logger.info(this.marker, s"${ctx.channel().remoteAddress().toString} uploaded ${file.getFilename}")
    }
  }
}
