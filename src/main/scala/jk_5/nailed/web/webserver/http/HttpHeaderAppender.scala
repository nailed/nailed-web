package jk_5.nailed.web.webserver.http

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel._
import io.netty.handler.codec.http._
import jk_5.nailed.web.NailedWeb
import java.util.Date
import io.netty.util.{AttributeKey, CharsetUtil}

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object HttpHeaderAppender extends ChannelDuplexHandler {

  val newline = "\r\n".getBytes(CharsetUtil.UTF_8)
  val request = AttributeKey.valueOf[HttpRequest]("request")

  override def write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise){
    var close = false
    msg match {
      case res: HttpResponse =>
        val req = ctx.attr(this.request).get
        res.headers().set(HttpHeaders.Names.SERVER, s"nailed-web/${NailedWeb.version}")
        res.headers().set(HttpHeaders.Names.DATE, HttpHeaderDateFormat.get.format(new Date))
        if(!res.headers().contains(HttpHeaders.Names.CONTENT_TYPE)){
          res.headers().add(HttpHeaders.Names.CONTENT_TYPE, "application/json")
        }
        res match {
          case e: FullHttpResponse =>
            val content = e.content()
            val length = content.readableBytes()
            if(req.getMethod == HttpMethod.HEAD){
              content.clear()
            }else if(req.getMethod != HttpMethod.HEAD && content.readableBytes() >= 2 && content.slice(content.readableBytes() - 2, 2).readBytes(2) != newline){
              content.writeBytes(this.newline)
            }
            if(!e.headers().contains(HttpHeaders.Names.CONTENT_LENGTH)){
              e.headers().set(HttpHeaders.Names.CONTENT_LENGTH, length)
            }
            if(HttpHeaders.isKeepAlive(req)){
              e.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
            }
            close = !HttpHeaders.isKeepAlive(req)
          case _ =>
        }
      case r: LastHttpContent =>
        val req = ctx.attr(this.request).get
        close = !HttpHeaders.isKeepAlive(req)
      case _ =>
    }
    ctx.write(msg, promise)
    if(close) promise.addListener(ChannelFutureListener.CLOSE)
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = msg match {
    case req: HttpRequest =>
      ctx.attr(this.request).set(req)
      ctx.fireChannelRead(req)
    case m => ctx.fireChannelRead(m)
  }
}
