package jk_5.nailed.web.webserver.websocket

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.{MessageToMessageEncoder, MessageToMessageDecoder}
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import java.util
import jk_5.nailed.web.webserver.packet.Packet
import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.webserver.PacketManager

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object WebSocketPacketDecoder extends MessageToMessageDecoder[TextWebSocketFrame] {
  def decode(ctx: ChannelHandlerContext, msg: TextWebSocketFrame, out: util.List[AnyRef]){
    try{
      val data = JsonObject.readFrom(msg.text())
      if(data == null || data.get("id") == null){
        out.add(msg)
        return
      }
      val packet = PacketManager.getPacketFromID(data.get("id").asString)
      if(packet == null){
        out.add(msg)
        return
      }
      if(data.get("data") != null){
        packet.read(data.get("data").asObject)
      }
      packet.setDecoder("websocket")
      out.add(packet)
    }catch{
      case e: Exception => out.add(msg); e.printStackTrace()
    }
  }
}

@Sharable
object WebSocketPacketEncoder extends MessageToMessageEncoder[Packet] {
  def encode(ctx: ChannelHandlerContext, msg: Packet, out: util.List[AnyRef]){
    val data = new JsonObject
    msg.write(data)
    val packetData = new JsonObject
    packetData.add("id", msg.getPacketID)
    if(msg.hasData) packetData.add("data", data)
    out.add(new TextWebSocketFrame(packetData.stringify))
  }
}
