package jk_5.nailed.web.webserver.socketio.transport.websocket

import io.netty.channel._
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.RoutedHandler
import java.util.UUID
import io.netty.handler.codec.http.websocketx.{CloseWebSocketFrame, WebSocketServerHandshakerFactory}
import jk_5.nailed.web.webserver.http.WebServerUtils
import jk_5.nailed.web.webserver.socketio.handler.WebServerHandlerSIOHandshake
import jk_5.nailed.web.webserver.socketio.{HeartbeatHandler, ClientRegistry}

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerSIOWebSocket extends ChannelInboundHandlerAdapter with RoutedHandler {

  private var request: HttpRequest = null

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = msg match {
    case msg: HttpRequest => this.request = msg
    case _: LastHttpContent =>
      val uid = UUID.fromString(this.getURLData.parameters.get("part2").get)
      this.logger.debug(marker, "Incoming websocket connection from client {}", uid)
      val future = WebServerHandlerSIOHandshake.futures.get(uid)
      val session = WebServerHandlerSIOHandshake.sessions.get(uid)
      if(future.isDefined && session.isDefined){
        future.get.cancel(true)
        val path = "ws://" + this.request.headers().get(HttpHeaders.Names.HOST) + this.request.getUri
        val factory = new WebSocketServerHandshakerFactory(path, null, false)
        val handshaker = factory.newHandshaker(this.request)
        val r = new DefaultFullHttpRequest(this.request.getProtocolVersion, this.request.getMethod, this.request.getUri)
        if(handshaker == null){
          WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel())
          r.release()
          return
        }
        handshaker.handshake(ctx.channel(), r).addListener(new ChannelFutureListener {
          override def operationComplete(future: ChannelFuture){
            val client = new WebsocketClient(future.channel(), uid, session.get)
            client.channel.pipeline().addBefore("routedHandler", "sioWebsocketCodec", WebsocketCodec)
            ClientRegistry.connect(client)
            HeartbeatHandler.onHeartbeat(client)
          }
        })
        r.release()
      }else WebServerUtils.sendError(ctx, HttpResponseStatus.UNAUTHORIZED)
    case _: HttpContent =>
    case msg: CloseWebSocketFrame =>
      ctx.channel().close()
      msg.release()
    case _ => ctx.fireChannelRead(msg)
  }
}
