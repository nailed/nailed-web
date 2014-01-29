package jk_5.nailed.web.webserver.websocket

import io.netty.handler.codec.http.websocketx._
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.ssl.NotSslRecordException
import jk_5.nailed.web.webserver.NetworkRegistry

/**
 * No description given
 *
 * @author jk-5
 */
class WebSocketHandler(private final val handshaker: WebSocketServerHandshaker) extends SimpleChannelInboundHandler[WebSocketFrame] {

  override def messageReceived(ctx: ChannelHandlerContext, msg: WebSocketFrame) = msg match {
    case t: TextWebSocketFrame => //TODO: This is invalid data! Handle it!
    case f: CloseWebSocketFrame => {
      val handler = ctx.channel().attr(NetworkRegistry.ATTR_NETWORKHANDLER).get()
      if(handler != null) handler.closeConnection("Client requested disconnect")
    }
    case f: PingWebSocketFrame => ctx.write(new PongWebSocketFrame(f.content().retain()))
    case f => throw new UnsupportedOperationException("%s frame types not supported".format(msg.getClass.getSimpleName))
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) = cause match{
    case e: NotSslRecordException => //TODO: Redirect to SSL
  }

  @inline def getHandshaker = this.handshaker
}
