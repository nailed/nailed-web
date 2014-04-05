package jk_5.nailed.web.webserver.socketio.packet

import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.channel.ChannelHandlerContext
import java.util
import io.netty.channel.ChannelHandler.Sharable
import jk_5.jsonlibrary.{JsonArray, JsonObject}

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object SIOPacketEncoder extends MessageToMessageEncoder[SIOPacket] {
  private val separator = ':'
  private val ack_data = "data"

  override def encode(ctx: ChannelHandlerContext, packet: SIOPacket, out: util.List[AnyRef]){
    val builder = new StringBuilder
    builder.append(packet.getType.getValue)
    builder.append(this.separator)

    val id = packet.id
    val endpoint = packet.endpoint
    val ack = packet.ack
    if(this.ack_data == ack){
      builder.append(id)
      builder.append('+')
    }else if(id != Long.MinValue){
      builder.append(id)
    }
    builder.append(this.separator)
    if(endpoint != null){
      builder.append(endpoint)
    }

    packet match {
      case p: MessageSIOPacket =>
        if(p.message != null){
          builder.append(this.separator)
          builder.append(p.message)
        }
      case p: JsonSIOPacket =>
        builder.append(this.separator)
        builder.append(p.json.stringify)
      case p: EventSIOPacket =>
        builder.append(this.separator)
        val data = new JsonObject().add("name", p.name)
        val list = new JsonArray
        data.add("args", list)
        p.args.foreach(list.add(_))
        builder.append(data.stringify)
      case _ =>
    }

    out.add(new EncodedSIOPacket(builder))
  }
}
