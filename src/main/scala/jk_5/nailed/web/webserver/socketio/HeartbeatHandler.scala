package jk_5.nailed.web.webserver.socketio

import java.util.concurrent.TimeUnit
import jk_5.nailed.web.webserver.socketio.packet.HeartbeatSIOPacket

/**
 * No description given
 *
 * @author jk-5
 */
object HeartbeatHandler {
  val heartbeatInterval = 25
  val heartbeatTimeout = 60

  def onHeartbeat(client: SIOClient){
    val ch = client.channel
    if(client.sendHeartbeatFuture != null) client.sendHeartbeatFuture.cancel(false)
    if(client.timeoutFuture != null) client.timeoutFuture.cancel(false)
    client.sendHeartbeatFuture = ch.eventLoop().schedule(new Runnable {
      override def run(){
        client.send(new HeartbeatSIOPacket)

        client.timeoutFuture = ch.eventLoop().schedule(new Runnable {
          override def run(){
            client.disconnect()
          }
        }, heartbeatTimeout, TimeUnit.SECONDS)
      }
    }, this.heartbeatInterval, TimeUnit.SECONDS)
  }

  def onDisconnect(client: SIOClient){
    if(client.sendHeartbeatFuture != null) client.sendHeartbeatFuture.cancel(false)
    if(client.timeoutFuture != null) client.timeoutFuture.cancel(false)
  }
}
