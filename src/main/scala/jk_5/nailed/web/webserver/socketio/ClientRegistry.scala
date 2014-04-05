package jk_5.nailed.web.webserver.socketio

import io.netty.channel.{ChannelFutureListener, Channel}
import java.util.UUID
import jk_5.nailed.web.auth.AuthSession
import io.netty.util.AttributeKey
import io.netty.util.concurrent.ScheduledFuture
import jk_5.nailed.web.webserver.socketio.packet._

/**
 * No description given
 *
 * @author jk-5
 */
object ClientRegistry {
  val sioClient: AttributeKey[SIOClient] = AttributeKey.valueOf("SIOClient")

  def connect(client: SIOClient){
    val pipe = client.channel.pipeline()
    pipe.addBefore("routedHandler", "sioPacketEncoder", SIOPacketEncoder)
    pipe.addBefore("routedHandler", "sioPacketDecoder", SIOPacketDecoder)
    pipe.addBefore("routedHandler", "sioPacketHandler", SIOPacketHandler)

    client.send(new ConnectSIOPacket)
  }

  def disconnect(client: SIOClient){
    HeartbeatHandler.onDisconnect(client)
  }
}

abstract class SIOClient(val channel: Channel, val uid: UUID, val session: AuthSession) {
  this.channel.attr(ClientRegistry.sioClient).set(this)

  var sendHeartbeatFuture: ScheduledFuture[_] = _
  var timeoutFuture: ScheduledFuture[_] = _

  def send(packet: SIOPacket) = this.channel.writeAndFlush(packet)
  def disconnect() = this.send(new DisconnectSIOPacket).addListener(ChannelFutureListener.CLOSE)
}
