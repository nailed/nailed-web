package jk_5.nailed.web.webserver.http.handlers

import jk_5.nailed.web.webserver.RoutedHandler
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.{HttpResponseStatus, HttpMethod, FullHttpRequest}
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
    if(msg.getMethod == HttpMethod.GET){
      val mappackId = this.getURLData.parameters.get("part1").get
      val mappack = MappackRegistry.getById(mappackId)
      if(mappack.isEmpty){
        WebServerUtils.sendError(ctx, "Mappack not found")
        return
      }
      WebServerUtils.sendOK(ctx, new JsonObject().add("mappack", mappack.get.toJson))
    }else WebServerUtils.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED)
  }
}
