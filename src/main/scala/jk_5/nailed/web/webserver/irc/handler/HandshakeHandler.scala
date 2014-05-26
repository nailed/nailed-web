package jk_5.nailed.web.webserver.irc.handler

import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import jk_5.nailed.web.webserver.irc.{UserConnection, ProtocolIrc}
import jk_5.nailed.web.webserver.SslDetector
import java.util.concurrent.TimeUnit
import io.netty.util.concurrent.ScheduledFuture

/**
 * No description given
 *
 * @author jk-5
 */
class HandshakeHandler extends ChannelInboundHandlerAdapter {

  var connection: UserConnection = _
  @volatile private var future: ScheduledFuture[_] = null

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = msg match {
    case frame: String =>
      if(this.connection == null){
        this.connection = new UserConnection(ctx.channel())
      }
      val parsed = ProtocolIrc.parseOperationAndArgs(frame)
      val args = parsed._2
      parsed._1 match {
        case "PASS" =>
          if(args.isEmpty) {
            ctx.writeAndFlush(s":${ProtocolIrc.host} 461 * PASS :Not enough parameters")
          } else {
            this.connection.password = args(0)
            if (this.connection.authenticate(force = true)) {
              ctx.channel().attr(ProtocolIrc.connection).set(this.connection)
              this.handshake(ctx)
            }
          }
        case "NICK" =>
          if(args.isEmpty) {
            ctx.writeAndFlush(s":${ProtocolIrc.host} 461 * NICK :Not enough parameters")
          } else if(args(0).isEmpty) {
            ctx.writeAndFlush(s":${ProtocolIrc.host} 431 * :No nickname given")
          } else {
            this.connection.nickname = args(0)
          }
        case "USER" =>
          if(args.length < 4){
            ctx.writeAndFlush(s":${ProtocolIrc.host} 461 * USER :Not enough parameters")
            return
          }
          this.connection.login = args(0)
          this.connection.realname = args(3)
          if(this.connection.authenticate(force = false)){
            ctx.channel().attr(ProtocolIrc.connection).set(this.connection)
            this.handshake(ctx)
          }else{
            ctx.writeAndFlush(s":${ProtocolIrc.host} NOTICE AUTH :*** You need to send your password. Try: /quote PASS <password>")
          }
        case "CAP" => //I don't see why i need to handle these
        case _ => ctx.writeAndFlush(s":${ProtocolIrc.host} 451 * :Please register first")
      }
    case m => ctx.fireChannelRead(m)
  }

  def handshake(ctx: ChannelHandlerContext){
    if(SslDetector.isSsl(ctx.channel())) ctx.write(s":${ProtocolIrc.host} NOTICE AUTH :*** You are using a secure connection")
    else ctx.write(s":${ProtocolIrc.host} NOTICE AUTH :*** You are using an insecure connection. Please connect with SSL enabled!")
    ctx.write(s":${ProtocolIrc.host} 001 ${this.connection.nickname} :Welcome ${this.connection.realname}, to nailed-web's internal IRC server")
    ctx.write(s":${ProtocolIrc.host} 002 ${this.connection.nickname} :Your host is ${ProtocolIrc.host}[0.0.0.0/6667], running version 0.1-SNAPSHOT")
    ctx.write(s":${ProtocolIrc.host} 003 ${this.connection.nickname} :This server was created April 9 2014 at 8:54:52")
    ctx.write(s":${ProtocolIrc.host} 004 ${this.connection.nickname} ${ProtocolIrc.host} nailed-web")
    ctx.write(s":${ProtocolIrc.host} 005 ${this.connection.nickname} CHANTYPES=${ProtocolIrc.channelPrefixes.mkString("")} TOPICLEN=350 CHANNELLEN=50 PREFIX=(qaohv)~&@%+ :are supported by this server")
    ctx.flush()

    this.connection.connected()
    ctx.pipeline().addAfter(ctx.name(), "conversationHandler", new ChannelConversationHandler(this.connection))
    ctx.pipeline().addAfter(ctx.name(), "pingHandler", new PingHandler(1, TimeUnit.MINUTES))
    ctx.pipeline().remove(this)

    this.future.cancel(false)
  }

  override def handlerAdded(ctx: ChannelHandlerContext){
    this.future = ctx.executor().schedule(new CloseConnectionTask(ctx), 1, TimeUnit.MINUTES)
  }

  private final class CloseConnectionTask(val ctx: ChannelHandlerContext) extends Runnable {
    override def run(){
      if(!this.ctx.channel().isOpen) return
      this.ctx.writeAndFlush(s"ERROR :Closing Link: ${connection.hostname} (Registration timed out)").addListener(ChannelFutureListener.CLOSE)
    }
  }
}
