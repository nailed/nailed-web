package jk_5.nailed.web.webserver.irc

import io.netty.channel.{ChannelFuture, ChannelFutureListener, Channel}
import java.net.InetSocketAddress
import jk_5.nailed.web.auth.{SessionManager, UserDatabase, AuthSession, User}

/**
 * No description given
 *
 * @author jk-5
 */
class UserConnection(val channel: Channel) extends IrcConnection(channel.remoteAddress().asInstanceOf[InetSocketAddress].getHostName) with AuthenticatedConnection {

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

  override def sendLine(line: String) = this.channel.writeAndFlush(line)

  override def disconnected(reason: String){
    if(this.channel.isOpen) this.channel.close()
    super.disconnected(reason)
  }

  override def onPrivateMessage(connection: IrcConnection, message: String) = this.sendLine(s"${connection.commandPrefix}PRIVMSG $nickname ${if(message.contains(' ')) ":" + message else message}")
  override def onChannelMessage(sender: IrcConnection, channel: IrcChannel, message: String) = this.sendLine(s"${sender.commandPrefix}PRIVMSG ${channel.name} ${if(message.contains(' ')) ":" + message else message}")
  override def onUserJoinedChannel(connection: IrcConnection, channel: IrcChannel) = this.sendLine(s"${connection.commandPrefix}JOIN :${channel.name}")

  override def getSession = this.session.getOrElse(null)
  override def getUser = this.user.getOrElse(null)
}
