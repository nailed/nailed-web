package jk_5.nailed.web.webserver

import scala.collection.immutable
import jk_5.nailed.web.webserver.packet.{PacketCloseConnection, Packet}
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import java.util.concurrent.Executors

/**
 * No description given
 *
 * @author jk-5
 */
object PacketManager {
  private final val packets = immutable.HashMap[String, Class[_ <: Packet]](
    "close" -> classOf[PacketCloseConnection]
  )

  def getPacketID(cl: Class[_ <: Packet]): String = this.packets.find(p => p._2 == cl).get._1
  def getPacketFromID(id: String): Packet = this.packets.get(id).getOrElse(null).newInstance()
}

@Sharable
object PacketHandler extends SimpleChannelInboundHandler[Packet] {

  final val worker = Executors.newCachedThreadPool()

  def messageReceived(ctx: ChannelHandlerContext, packet: Packet){
    ctx.channel().attr(NetworkRegistry.ATTR_NETWORKHANDLER).setIfAbsent(new DummyNetworkHandler(ctx.channel()))
    val handler = ctx.channel().attr(NetworkRegistry.ATTR_NETWORKHANDLER).get()
    this.worker.execute(new ProcessPacketTask(packet, handler))
  }

  override def userEventTriggered(ctx: ChannelHandlerContext, event: AnyRef){
    ctx.fireUserEventTriggered(event)
    ctx.channel().attr(NetworkRegistry.ATTR_NETWORKHANDLER).setIfAbsent(new DummyNetworkHandler(ctx.channel()))
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
