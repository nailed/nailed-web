package jk_5.nailed.web.webserver.socketio.transport.websocket

import io.netty.handler.codec.MessageToMessageCodec
import io.netty.channel.ChannelHandlerContext
import java.util
import io.netty.channel.ChannelHandler.Sharable
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import jk_5.nailed.web.webserver.socketio.packet.EncodedSIOPacket

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object WebsocketCodec extends MessageToMessageCodec[TextWebSocketFrame, EncodedSIOPacket] {
  override def encode(ctx: ChannelHandlerContext, packet: EncodedSIOPacket, out: util.List[AnyRef]){
    out.add(new TextWebSocketFrame(Unpooled.copiedBuffer(packet.data, CharsetUtil.UTF_8)))
  }
  override def decode(ctx: ChannelHandlerContext, msg: TextWebSocketFrame, out: util.List[AnyRef]){
    out.add(new EncodedSIOPacket(msg.text()))
  }
}
