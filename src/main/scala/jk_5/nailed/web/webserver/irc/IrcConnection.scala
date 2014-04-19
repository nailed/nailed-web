package jk_5.nailed.web.webserver.irc

import io.netty.channel.ChannelFuture
import java.net.InetSocketAddress
import jk_5.nailed.web.auth.{UserDatabase, AuthSession}

/**
 * No description given
 *
 * @author jk-5
 */
abstract class IrcConnection(val hostname: String = ProtocolIrc.host) {

  var password: String = null
  var nickname: String = null
  var login: String = null
  var realname: String = null

  def connected(){
    ProtocolIrc.onConnect(this)
    ProtocolIrc.logger.info(ProtocolIrc.marker, s"User ${this.realname} connected to IRC")
  }

  def disconnected(r: String){
    var reason = r
    if(reason == ":" || reason == ""){
      reason = "No reason given"
    }
    ProtocolIrc.onDisconnect(this)
    ProtocolIrc.logger.info(ProtocolIrc.marker, s"User ${this.realname} disconnected from IRC ($reason)")
  }

  def join(channel: IrcChannel) = channel.onJoin(this)
  def part(channel: IrcChannel) = channel.onPart(this)
  def part(channel: IrcChannel, reason: String) = channel.onPart(this, reason)
  def sendMessage(channel: IrcChannel, message: String) = channel.onMessage(this, message)
  def sendMessage(connection: IrcConnection, message: String) = connection.onPrivateMessage(this, message)
  def onTopicRequest(channel: IrcChannel) = channel.onTopicRequest(this)
  def setTopic(channel: IrcChannel, newTopic: String) = channel.onSetTopic(this, newTopic)

  def onPrivateMessage(connection: IrcConnection, message: String){}
  def onChannelMessage(sender: IrcConnection, channel: IrcChannel, message: String){}
  def onUserJoinedChannel(connection: IrcConnection, channel: IrcChannel){}

  def commandPrefix = s":${this.nickname}!${this.login}@${this.hostname} "

  def sendLine(line: String){}

  final def setAllNames(name: String){
    this.nickname = name
    this.login = name
    this.realname = name
  }

  def noJoinNeeded = false
}
