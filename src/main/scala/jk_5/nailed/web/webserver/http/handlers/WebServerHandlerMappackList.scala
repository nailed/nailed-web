package jk_5.nailed.web.webserver.http.handlers

import jk_5.nailed.web.webserver.RoutedHandler
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import jk_5.jsonlibrary.{JsonArray, JsonObject}
import jk_5.nailed.web.webserver.http.WebServerUtils

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerMappackList extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {

  override def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    if(msg.getMethod == HttpMethod.GET || msg.getMethod == HttpMethod.HEAD){
      val list = new JsonArray
      val data = new JsonObject().add("mappacks", list)
      WebServerUtils.sendOK(ctx, data)
    }else WebServerUtils.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED)
  }
}
