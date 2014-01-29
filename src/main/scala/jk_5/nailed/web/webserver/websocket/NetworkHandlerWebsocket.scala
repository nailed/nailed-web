package jk_5.nailed.web.webserver.websocket

import io.netty.channel.{ChannelFuture, Channel}
import io.netty.handler.codec.http.websocketx.{CloseWebSocketFrame, WebSocketServerHandshaker}
import jk_5.nailed.web.webserver.{NullConnection, Connection, NetworkHandler}
import jk_5.nailed.web.webserver.packet.PacketCloseConnection

/**
 * No description given
 *
 * @author jk-5
 */
class NetworkHandlerWebsocket(_channel: Channel, _connection: Connection = new NullConnection) extends NetworkHandler(_channel, _connection) {

  private val handshaker: WebSocketServerHandshaker = this.getChannel.pipeline().get(classOf[WebSocketHandler]).getHandshaker

  override def closeConnection(reason: String): ChannelFuture = {
    this.sendPacket(new PacketCloseConnection(reason))
    this.handshaker.close(this.getChannel, new CloseWebSocketFrame(1000, reason))
  }
}
