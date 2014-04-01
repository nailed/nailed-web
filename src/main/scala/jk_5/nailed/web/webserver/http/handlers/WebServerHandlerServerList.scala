package jk_5.nailed.web.webserver.http.handlers

import jk_5.nailed.web.webserver.RoutedHandler
import io.netty.handler.codec.http.{HttpResponseStatus, HttpMethod, FullHttpRequest}
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import jk_5.jsonlibrary.{JsonObject, JsonArray}
import jk_5.nailed.web.webserver.http.WebServerUtils
import jk_5.nailed.web.game.ServerRegistry

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerServerList extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {
  override def messageReceived(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    if(msg.getMethod == HttpMethod.GET){
      val list = new JsonArray
      ServerRegistry.getServers.foreach(s => list.add(s.toJson))
      val data = new JsonObject().add("servers", list)
      WebServerUtils.sendOK(ctx, data)
    }else WebServerUtils.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED)
  }
}
