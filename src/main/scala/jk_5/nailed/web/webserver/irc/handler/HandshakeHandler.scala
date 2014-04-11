package jk_5.nailed.web.webserver.irc.handler

import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import jk_5.nailed.web.webserver.irc.{ProtocolIrc, IrcConnection}

/**
 * No description given
 *
 * @author jk-5
 */
class HandshakeHandler extends ChannelInboundHandlerAdapter {

  var connection: IrcConnection = _

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = msg match {
    case frame: String =>
      if(this.connection == null){
        this.connection = new IrcConnection(ctx.channel())
      }
      if(frame.startsWith("PASS")){
        this.connection.password = frame.substring(5).trim
        if(this.connection.authenticate(force = true)){
          ctx.channel().attr(ProtocolIrc.connection).set(this.connection)
          this.handshake(ctx)
        }
      }else if(frame.startsWith("NICK")){
        this.connection.nickname = frame.substring(5).trim
      }else if(frame.startsWith("USER")){
        this.connection.login = "~" + frame.substring(5, frame.indexOf(' ', 6)).trim
        this.connection.realname = frame.substring(frame.indexOf('*') + 1).trim
        if(this.connection.realname.startsWith(":")){
          this.connection.realname = this.connection.realname.substring(1).trim
        }
        if(this.connection.authenticate(force = false)){
          ctx.channel().attr(ProtocolIrc.connection).set(this.connection)
          this.handshake(ctx)
        }else{
          ctx.writeAndFlush(s":${ProtocolIrc.host} NOTICE AUTH :*** You need to send your password. Try: /quote PASS <password>")
        }
      }else{
        ctx.fireChannelRead(msg)
        return
      }
    case m => ctx.fireChannelRead(m)
  }

  def handshake(ctx: ChannelHandlerContext){
    ctx.write(s":${ProtocolIrc.host} NOTICE AUTH :*** You are using a secure connection")
    ctx.write(s":${ProtocolIrc.host} 001 ${this.connection.nickname} :Welcome ${this.connection.realname}, to nailed-web's internal IRC server")
    ctx.write(s":${ProtocolIrc.host} 002 ${this.connection.nickname} :The host is ${ProtocolIrc.host}[0.0.0.0/6667], running version 0.1-SNAPSHOT")
    ctx.write(s":${ProtocolIrc.host} 003 ${this.connection.nickname} :This server was created April 9 2014 at 8:54:52")
    ctx.write(s":${ProtocolIrc.host} 004 ${this.connection.nickname} ${ProtocolIrc.host} nailed-web")
    ctx.write(s":${ProtocolIrc.host} 005 ${this.connection.nickname} CHANTYPES=#& TOPICLEN=350 CHANNELLEN=50")
    ctx.flush()

    this.connection.connected()
    ctx.pipeline().addAfter(ctx.name(), "conversationHandler", new ChannelConversationHandler(this.connection))
    ctx.pipeline().remove(this)
  }
}
