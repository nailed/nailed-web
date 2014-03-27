package jk_5.nailed.web.webserver.http

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerAdapter, ChannelPromise, ChannelHandlerContext}
import io.netty.handler.codec.http.{FullHttpResponse, HttpHeaders, HttpResponse}
import jk_5.nailed.web.NailedWeb
import java.util.Date

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object HttpHeaderAppender extends ChannelHandlerAdapter {
  override def write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise){
    msg match {
      case res: HttpResponse => {
        res.headers().set(HttpHeaders.Names.SERVER, "nailed-web/%s".format(NailedWeb.version))
        res.headers().set(HttpHeaders.Names.DATE, WebServerUtils.formatter.format(new Date))
        if(!res.headers().contains(HttpHeaders.Names.CONTENT_TYPE)) res.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/json")
        res match{
          case e: FullHttpResponse =>
            if(!e.headers().contains(HttpHeaders.Names.CONTENT_LENGTH)){
              e.headers().set(HttpHeaders.Names.CONTENT_LENGTH, e.content().readableBytes())
            }
          case e =>
        }
      }
      case e =>
    }
    ctx.write(msg, promise)
  }
}
