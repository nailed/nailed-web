package jk_5.nailed.web.webserver.irc.handler

import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import jk_5.nailed.web.webserver.irc.ProtocolIrc

/**
 * No description given
 *
 * @author jk-5
 */
class PingHandler(val timeout: Int, val unit: TimeUnit) extends ChannelInboundHandlerAdapter {

  @volatile private var future: ScheduledFuture[_] = null
  private var task: PingTask = _
  @volatile private var ticks = 0

  override def handlerAdded(ctx: ChannelHandlerContext){
    this.task = new PingTask(ctx)
    this.future = ctx.executor().schedule(this.task, this.timeout, this.unit)
  }

  override def handlerRemoved(ctx: ChannelHandlerContext){
    if(this.future != null){
      this.future.cancel(false)
      this.future = null
    }
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = msg match {
    case frame: String =>
      if(frame.startsWith("PONG")){
        if(this.future != null){
          this.future.cancel(false)
          this.future = null
        }
        this.ticks = 0
        this.future = ctx.executor().schedule(this.task, this.timeout, this.unit)
      }else ctx.fireChannelRead(msg)
    case _ => ctx.fireChannelRead(msg)
  }

  private final class PingTask(val ctx: ChannelHandlerContext) extends Runnable {
    override def run(){
      if(!this.ctx.channel().isOpen) return
      if(ticks >= 1){
        this.ctx.writeAndFlush(s":${ProtocolIrc.host} NOTICE AUTH : Ping timeout. Closing connection").addListener(ChannelFutureListener.CLOSE)
        ctx.channel().attr(ProtocolIrc.connection).get().disconnected("Ping timeout: 60 seconds")
        return
      }
      future = this.ctx.executor().schedule(this, timeout, unit)
      this.ctx.writeAndFlush(s"PING :${ProtocolIrc.host}")
      ticks += 1
    }
  }
}
