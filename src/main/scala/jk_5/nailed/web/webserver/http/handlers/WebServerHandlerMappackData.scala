package jk_5.nailed.web.webserver.http.handlers

import jk_5.nailed.web.webserver.RoutedHandler
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.FullHttpRequest
import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.mappack.MappackRegistry
import jk_5.nailed.web.webserver.http.WebServerUtils

/**
  * No description given
  *
  * @author jk-5
  */
class WebServerHandlerMappackData extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {

  def messageReceived(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    val mappackId = this.getURLData.getParameters.get("part1").get
    val mappack = MappackRegistry.getById(mappackId)
    if(mappack.isEmpty){
      WebServerUtils.sendJson(ctx, new JsonObject().add("status", "error").add("error", "Mappack not found"))
      return
    }
    WebServerUtils.sendJson(ctx, new JsonObject().add("status", "ok").add("mappack", mappack.get.toJson))
  }
}
