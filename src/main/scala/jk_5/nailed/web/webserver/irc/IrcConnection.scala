package jk_5.nailed.web.webserver.irc

import io.netty.channel.{ChannelFutureListener, ChannelFuture, Channel}
import java.net.InetSocketAddress
import jk_5.nailed.web.auth.{User, UserDatabase, SessionManager, AuthSession}

/**
 * No description given
 *
 * @author jk-5
 */
class IrcConnection(val channel: Channel) {

  var password: String = null
  var nickname: String = null
  var login: String = null
  var realname: String = null
  val hostname = this.channel.remoteAddress().asInstanceOf[InetSocketAddress].getHostName
  private val connFuture = this.channel.newPromise()
  var user: Option[User] = None
  var session: Option[AuthSession] = None

  this.channel.closeFuture().addListener(new ChannelFutureListener {
    override def operationComplete(future: ChannelFuture) = disconnected("Remote host closed the connection")
  })

  def authenticate(force: Boolean): Boolean = {
    if(force && this.password == null){
      this.channel.writeAndFlush(s":${ProtocolIrc.host} ${ReplyCodes.ERR_PASSWDMISMATCH} :No password given").addListener(ChannelFutureListener.CLOSE)
      return false
    }
    if(this.nickname == null || this.login == null || this.realname == null || this.hostname == null){
      return false
    }
    if(this.user.isEmpty){
      this.user = UserDatabase.getUserByUsername(this.nickname)
      if(this.user.isEmpty){
        this.channel.writeAndFlush(s":${ProtocolIrc.host} ${ReplyCodes.ERR_NOSUCHNICK} :No user was found matching your nickname").addListener(ChannelFutureListener.CLOSE)
        return false
      }
    }
    if(this.session.isEmpty){
      if(this.password == null){
        return false
      }
      this.session = SessionManager.getSession(this.user.get, this.password)
      if(this.session.isEmpty){
        this.channel.writeAndFlush(s":${ProtocolIrc.host} ${ReplyCodes.ERR_PASSWDMISMATCH} :Invalid password").addListener(ChannelFutureListener.CLOSE)
        return false
      }
    }
    this.channel.writeAndFlush(s":${ProtocolIrc.host} NOTICE AUTH :*** Authenticated successfully")
    if(this.login.startsWith("~")){
      this.login = login.substring(1)
    }
    this.realname = this.user.get.getFullName
    true
  }

  def connected(){
    this.connFuture.setSuccess()
    ProtocolIrc.logger.info(ProtocolIrc.marker, s"User ${this.realname} connected to IRC")
  }

  def disconnected(r: String){
    var reason = r
    if(this.channel.isOpen) this.channel.close()
    if(reason == ":" || reason == ""){
      reason = "No reason given"
    }
    ProtocolIrc.logger.info(ProtocolIrc.marker, s"User ${this.realname} disconnected from IRC ($reason)")
  }

  def join(channel: IrcChannel) = channel.onJoin(this)
  def part(channel: IrcChannel) = channel.onPart(this)
  def part(channel: IrcChannel, reason: String) = channel.onPart(this, reason)
  def sendMessage(channel: IrcChannel, message: String) = channel.onMessage(this, message)
  def onTopic(channel: IrcChannel) = channel.onTopic(this)
  def setTopic(channel: IrcChannel, newTopic: String) = channel.onSetTopic(this, newTopic)

  def commandPrefix = s":${this.nickname}!${this.login}@${this.hostname} "
  def connectFuture: ChannelFuture = this.connFuture
}
