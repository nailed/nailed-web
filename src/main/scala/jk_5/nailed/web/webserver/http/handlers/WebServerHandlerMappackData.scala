package jk_5.nailed.web.webserver.http.handlers

import jk_5.nailed.web.webserver.RoutedHandler
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.{HttpResponseStatus, HttpVersion, DefaultFullHttpResponse, FullHttpRequest}
import io.netty.buffer.Unpooled
import jk_5.jsonlibrary.JsonObject
import io.netty.util.CharsetUtil

/**
  * No description given
  *
  * @author jk-5
  */
class WebServerHandlerMappackData extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {

  def messageReceived(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    val data = new JsonObject().add("status", "ok").add("id", "nail")
    ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(data.stringify, CharsetUtil.UTF_8)))
  }
}
