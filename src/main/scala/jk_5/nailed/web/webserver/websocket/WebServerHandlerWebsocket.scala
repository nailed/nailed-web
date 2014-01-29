package jk_5.nailed.web.webserver.websocket

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.{HttpHeaders, FullHttpRequest}
import jk_5.nailed.web.webserver.{NetworkRegistry, PacketHandler, RoutedHandler}
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.handler.timeout.IdleStateHandler
import jk_5.nailed.web.webserver.http.HttpHeaderAppender

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerWebsocket extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {
  def messageReceived(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    val factory = new WebSocketServerHandshakerFactory("%s://".format(/*if(SslContextProvider.isValid) "wss" else */"ws") + msg.headers().get(HttpHeaders.Names.HOST) + this.getURLData.getURL, null, false)
    val handshaker = factory.newHandshaker(msg)
    if(handshaker == null) WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel())
    else{
      handshaker.handshake(ctx.channel(), msg)
      val handler = new WebSocketHandler(handshaker)
      val pipe = ctx.pipeline()
      pipe.remove(HttpHeaderAppender.getClass)
      pipe.remove(classOf[ChunkedWriteHandler])
      pipe.addLast("packetWetSocketDecoder", WebSocketPacketDecoder)
      pipe.addLast("packetWetSocketEncoder", WebSocketPacketEncoder)
      pipe.addLast("idleStateHandler", new IdleStateHandler(10, 30, 60))
      pipe.addLast("packetHandler", PacketHandler)
      pipe.addLast("websocketHandler", handler)
      ctx.channel().attr(NetworkRegistry.ATTR_NETWORKHANDLER).set(new NetworkHandlerWebsocket(ctx.channel()))
    }
  }
}
