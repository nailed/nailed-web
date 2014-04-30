package jk_5.nailed.web.webserver.irc.handler

import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import jk_5.nailed.web.webserver.irc.{UserConnection, ProtocolIrc}
import jk_5.nailed.web.webserver.SslDetector

/**
 * No description given
 *
 * @author jk-5
 */
class HandshakeHandler extends ChannelInboundHandlerAdapter {

  var connection: UserConnection = _

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = msg match {
    case frame: String =>
      if(this.connection == null){
        this.connection = new UserConnection(ctx.channel())
      }
      val operation = frame.substring(0, frame.indexOf(' '))
      val args = frame.substring(frame.indexOf(' ') + 1)
      operation match {
        case "PASS" =>
          this.connection.password = args
          if(this.connection.authenticate(force = true)){
            ctx.channel().attr(ProtocolIrc.connection).set(this.connection)
            this.handshake(ctx)
          }
        case "NICK" => this.connection.nickname = frame.substring(5).trim
        case "USER" =>
          this.connection.login = "~" + args.substring(0, args.indexOf(' '))
          this.connection.realname = args.substring(args.indexOf('*') + 1).trim
          if(this.connection.realname.startsWith(":")){
            this.connection.realname = this.connection.realname.substring(1).trim
          }
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
    ctx.write(s":${ProtocolIrc.host} 002 ${this.connection.nickname} :The host is ${ProtocolIrc.host}[0.0.0.0/6667], running version 0.1-SNAPSHOT")
    ctx.write(s":${ProtocolIrc.host} 003 ${this.connection.nickname} :This server was created April 9 2014 at 8:54:52")
    ctx.write(s":${ProtocolIrc.host} 004 ${this.connection.nickname} ${ProtocolIrc.host} nailed-web")
    ctx.write(s":${ProtocolIrc.host} 005 ${this.connection.nickname} CHANTYPES=# TOPICLEN=350 CHANNELLEN=50 PREFIX=(qaohv)~&@%+")
    ctx.flush()

    this.connection.connected()
    ctx.pipeline().addAfter(ctx.name(), "conversationHandler", new ChannelConversationHandler(this.connection))
    ctx.pipeline().remove(this)
  }
}
