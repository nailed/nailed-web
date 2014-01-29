package jk_5.nailed.web.webserver

import io.netty.channel.{Channel, ChannelFuture}
import io.netty.handler.timeout.{IdleStateEvent, IdleState}
import io.netty.util.AttributeKey
import jk_5.nailed.web.webserver.packet.{PacketCloseConnection, Packet}

/**
 * No description given
 *
 * @author jk-5
 */
object NetworkRegistry {
  final val ATTR_NETWORKHANDLER: AttributeKey[NetworkHandler] = AttributeKey.valueOf("NetworkHandler")
}

abstract class NetworkHandler(private final val channel: Channel, private var connection: Connection = new NullConnection) {

  def sendPacket(packet: Packet) = this.channel.writeAndFlush(packet)
  def closeConnection(reason: String): ChannelFuture = {
    this.sendPacket(new PacketCloseConnection(reason))
    this.channel.close()
  }
  def closeConnection(): ChannelFuture = this.closeConnection("No reason given")

  def onPipelineEvent(event: AnyRef){
    event match {
      case e: IdleStateEvent => e.state() match {
        case IdleState.READER_IDLE => if(!this.connection.isAuthenticated){
          this.closeConnection("Not authenticated after 10 seconds!")
        }
        case IdleState.WRITER_IDLE => //TODO: Try ping
        case IdleState.ALL_IDLE => //TODO: Not sure what to do here? Just close it?
      }
      case e =>
    }
  }

  final def getChannel = this.channel
  final def getConnection = this.connection
  final def setConnection(connection: Connection) = this.connection = connection
}
