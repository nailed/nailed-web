package jk_5.nailed.web.webserver

import scala.collection.immutable
import jk_5.nailed.web.webserver.packet._
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import jk_5.nailed.web.NailedWeb

/**
 * No description given
 *
 * @author jk-5
 */
object PacketManager {
  private final val packets = immutable.HashMap[String, Class[_ <: Packet]](
    "close" -> classOf[PacketCloseConnection],
    "keepalive" -> classOf[PacketKeepAlive],
    "auth" -> classOf[PacketAuthenticate],
    "authResponse" -> classOf[PacketAuthResponse]
  )

  def getPacketID(cl: Class[_ <: Packet]): String = this.packets.find(p => p._2 == cl).get._1
  def getPacketFromID(id: String): Packet = this.packets.get(id).getOrElse(null).newInstance()
}

@Sharable
object PacketHandler extends SimpleChannelInboundHandler[Packet] {

  def messageReceived(ctx: ChannelHandlerContext, packet: Packet){
    val handler = ctx.channel().attr(NetworkRegistry.ATTR_NETWORKHANDLER).get()
    NailedWeb.worker.execute(new ProcessPacketTask(packet, handler))
  }

  override def userEventTriggered(ctx: ChannelHandlerContext, event: AnyRef){
    ctx.fireUserEventTriggered(event)
    ctx.channel().attr(NetworkRegistry.ATTR_NETWORKHANDLER).get().onPipelineEvent(event)
  }

  class ProcessPacketTask(packet: Packet, handler: NetworkHandler) extends Runnable{
    if(packet == null) throw new NullPointerException("packet")
    def run(){
      try{
        packet.processPacket(handler)
      }catch{
        case e: Exception => {
          System.err.println("Error occurred while handling packet!")
          e.printStackTrace()
          System.err.println("  Network Handler " + handler)
          System.err.println("  Packet " + packet)
          System.err.println("  Packet ID " + packet.getPacketID)
          System.err.println("  Packet Decoder " + packet.getDecoder)
        }
      }
    }
  }
}
