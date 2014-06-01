package jk_5.nailed.web.webserver.http.apihandlers

import jk_5.nailed.web.webserver.http.handlers.AggregatedHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.http.response.ErrorResponse
import jk_5.nailed.web.auth.mojang.{UUIDCache, PlayerSkinLookup}
import jk_5.nailed.web.webserver.{MimeTypesLookup, RoutedHandler}
import jk_5.nailed.web.auth.mojang.PlayerSkinLookup.SkinCallback
import org.asynchttpclient.AsyncHttpClient
import java.util.concurrent.Executors
import io.netty.handler.stream.ChunkedStream

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
        UUIDCache.uuid(p, uuid => {
          PlayerSkinLookup.getSkin(uuid, new SkinCallback {
            override def onError() = println("Lookup error!")
            override def onSuccess(url: String){
              val future = ApiHandlerPlayerSkin.httpClient.executeRequest(ApiHandlerPlayerSkin.httpClient.prepareGet(url).build)
              future.addListener(new Runnable {
                override def run(){
                  println("Downloaded skin " + url)
                  val res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
                  res.headers().set(HttpHeaders.Names.CONTENT_TYPE, MimeTypesLookup.getMimeTypeFromExt("png"))
                  res.headers().set(HttpHeaders.Names.CONTENT_LENGTH, future.get().getResponseBodyAsBytes.length)
                  ctx.write(res)
                  ctx.write(new HttpChunkedInput(new ChunkedStream(future.get.getResponseBodyAsStream)))
                  ctx.write(LastHttpContent.EMPTY_LAST_CONTENT)
                  ctx.flush()
                }
              }, ApiHandlerPlayerSkin.executor)
            }
          })
        })
    }
  }
}
