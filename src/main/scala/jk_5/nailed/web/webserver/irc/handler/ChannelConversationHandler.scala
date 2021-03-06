package jk_5.nailed.web.webserver.irc.handler

import jk_5.nailed.web.webserver.irc.{ProtocolIrc, IrcConnection}
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}

/**
 * No description given
 *
 * @author jk-5
 */
class ChannelConversationHandler(val connection: IrcConnection) extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = msg match {
    case frame: String =>
      val operation = frame.substring(0, frame.indexOf(' '))
      val args = frame.substring(frame.indexOf(' ') + 1)
      operation match {
        case "QUIT" => this.connection.disconnected(if(args.startsWith(":")) args.substring(1) else args)
        case "JOIN" =>
          val parts = args.split(" ", 2)
          parts(0).split(",").foreach(c => {
            val valid = ProtocolIrc.channelPrefixes.exists(p => c.startsWith(p))
            if(valid) this.connection join ProtocolIrc.getOrCreateChannel(c)
          })
        case "PART" =>
          val parts = args.split(" ", 2)
          if(parts.length == 1){
            ProtocolIrc.getChannel(parts(0)).foreach(this.connection.part)
          }else{
            ProtocolIrc.getChannel(parts(0)).foreach(c => this.connection.part(c, parts(1)))
          }
        case "PRIVMSG" =>
          val parts = args.split(" ", 2)
          val msg = if(parts(1).startsWith(":")) parts(1).substring(1) else parts(1)
          if(ProtocolIrc.channelPrefixes.exists(p => parts(0).startsWith(p))){ //Sent to a channel
            ProtocolIrc.getChannel(parts(0)).foreach(c => this.connection.sendMessage(c, msg))
          }else{ //Sent to a user
            val conns = ProtocolIrc.getConnections(parts(0))
            if(conns.isEmpty){
              this.connection.sendLine(s":${ProtocolIrc.host} 401 ${this.connection.nickname} ${parts(0)} :No such nick")
            }else{
              conns.foreach(c => this.connection.sendMessage(c, msg))
            }
          }
        case "MODE" =>
          val chan = ProtocolIrc.getChannel(args)
          if(chan.isDefined){
            chan.get.onMode(this.connection)
          }
        case "TOPIC" =>
          val parts = args.split(" ", 2)
          if(parts.length == 1){
            ProtocolIrc.getChannel(parts(0)).foreach(c => this.connection.onTopicRequest(c))
          }else{
            ProtocolIrc.getChannel(parts(0)).foreach(c => this.connection.setTopic(c, parts(1)))
          }
        case "NICK" => //TODO: broadcast
          this.connection.nickname = args
        case "WHO" =>
          ProtocolIrc.getChannel(args).foreach(c => this.connection.onWhoRequest(c))
        case _ => this.connection.sendLine(s":${ProtocolIrc.host} 421 ${this.connection.nickname} $operation :Unknown command")
      }
    case m => ctx.fireChannelRead(m)
  }
}
