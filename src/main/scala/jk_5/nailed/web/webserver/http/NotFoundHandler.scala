package jk_5.nailed.web.webserver.http

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.{HttpResponseStatus, FullHttpRequest}

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object NotFoundHandler extends SimpleChannelInboundHandler[FullHttpRequest] {

  def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    val future = WebServerUtils.sendError(ctx, HttpResponseStatus.NOT_FOUND)
    WebServerUtils.closeIfRequested(msg, future)
  }
}
