package jk_5.nailed.web.webserver.http.routing

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{SimpleChannelInboundHandler, ChannelHandlerContext}
import io.netty.handler.codec.http._
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
class RoutingHandler extends SimpleChannelInboundHandler[FullHttpRequest] {

  private val routeDeclarations = new RouteDeclaration

  val response = Unpooled.copiedBuffer("HTTP/1.1 200 OK\nContent-Type: text/plain\nContent-Length: 3\nConnection: keep-alive\n\nHai\n", CharsetUtil.UTF_8)

  override def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer("Hai", CharsetUtil.UTF_8))
    response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/plain")
    response.headers().add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
    response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, 3)
    //ctx.pipeline().firstContext().writeAndFlush(Unpooled.copiedBuffer(response))//.addListener(ChannelFutureListener.CLOSE)
  }

  def uri(uri: String, handler: RequestHandler) = routeDeclarations.uri(uri, handler)
}
