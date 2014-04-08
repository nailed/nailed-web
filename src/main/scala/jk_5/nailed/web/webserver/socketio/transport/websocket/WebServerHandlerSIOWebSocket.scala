package jk_5.nailed.web.webserver.socketio.transport.websocket

import io.netty.channel._
import io.netty.handler.codec.http.{HttpResponseStatus, HttpHeaders, FullHttpRequest}
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
class WebServerHandlerSIOWebSocket extends ChannelHandlerAdapter with RoutedHandler {

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = msg match {
    case msg: FullHttpRequest =>
      val uid = UUID.fromString(this.getURLData.parameters.get("part2").get)
      this.logger.debug(marker, "Incoming websocket connection from client {}", uid)
      val future = WebServerHandlerSIOHandshake.futures.get(uid)
      val session = WebServerHandlerSIOHandshake.sessions.get(uid)
      if(future.isDefined && session.isDefined){
        future.get.cancel(true)
        val path = "ws://" + msg.headers().get(HttpHeaders.Names.HOST) + msg.getUri
        val factory = new WebSocketServerHandshakerFactory(path, null, false)
        val handshaker = factory.newHandshaker(msg)
        if(handshaker == null){
          WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel())
          msg.release()
          return
        }
        handshaker.handshake(ctx.channel(), msg).addListener(new ChannelFutureListener {
          override def operationComplete(future: ChannelFuture){
            val client = new WebsocketClient(future.channel(), uid, session.get)
            client.channel.pipeline().addBefore("routedHandler", "sioWebsocketCodec", WebsocketCodec)
            ClientRegistry.connect(client)
            HeartbeatHandler.onHeartbeat(client)
          }
        })
        msg.release()
      }else{
        val future = WebServerUtils.sendError(ctx, HttpResponseStatus.UNAUTHORIZED)
        WebServerUtils.closeIfRequested(msg, future)
        msg.release()
      }
    case msg: CloseWebSocketFrame =>
      ctx.channel().close()
      msg.release()
    case _ => ctx.fireChannelRead(msg)
  }
}
