package jk_5.nailed.web.webserver.http.apihandlers

import jk_5.nailed.web.webserver.http.handlers.AggregatedHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.http.response.ErrorResponse
import jk_5.nailed.web.webserver.{MimeTypesLookup, RoutedHandler}
import org.asynchttpclient.AsyncHttpClient
import java.util.concurrent.Executors
import io.netty.handler.stream.ChunkedStream
import jk_5.nailed.web.mojang.UuidCache
import java.io.ByteArrayInputStream

/**
 * No description given
 *
 * @author jk-5
 */
object ApiHandlerPlayerSkin {
  val httpClient = new AsyncHttpClient
  val executor = Executors.newCachedThreadPool
}
class ApiHandlerPlayerSkin extends AggregatedHandler with RoutedHandler {

  override def handleAggregated(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    if(msg.getMethod != HttpMethod.GET){
      ctx.writeAndFlush(new ErrorResponse(HttpResponseStatus.BAD_REQUEST))
      return
    }
    val part = this.getURLData.parameters.get("part1")
    part match {
      case None => ctx.writeAndFlush(new ErrorResponse(HttpResponseStatus.NOT_FOUND))
      case Some(p) =>
        UuidCache.fromUsername(p){
          entry => {
            val bytes = entry.getSkinBytes
            val res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
            res.headers().set(HttpHeaders.Names.CONTENT_TYPE, MimeTypesLookup.getMimeTypeFromExt("png"))
            res.headers().set(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
            ctx.write(res)
            ctx.write(new HttpChunkedInput(new ChunkedStream(new ByteArrayInputStream(bytes))))
            ctx.write(LastHttpContent.EMPTY_LAST_CONTENT)
            ctx.flush()
          }
        }
    }
  }
}
