package jk_5.nailed.web.webserver.irc

/**
 * No description given
 *
 * @author jk-5
 */
class IrcConnection {

  private var isConnected = false
  var hostname: String = ProtocolIrc.host
  var password: String = null
  var nickname: String = null
  var login: String = null
  var realname: String = null

  def this(name: String){
    this()
    this.setAllNames(name)
  }

  def connected(){
    if(this.isConnected) return
    ProtocolIrc.onConnect(this)
    ProtocolIrc.logger.info(ProtocolIrc.marker, s"User ${this.realname} connected to IRC")
    isConnected = true
  }

  def disconnected(r: String){
    if(!isConnected) return //We're not connected anyway. Ignore the call
    val reason = if(r.isEmpty) "No reason given" else r
    ProtocolIrc.channels.filter(_.connections.contains(this)).foreach(_.onQuit(this, reason))
    ProtocolIrc.onDisconnect(this)
    ProtocolIrc.logger.info(ProtocolIrc.marker, s"User ${this.realname} disconnected from IRC ($reason)")
    isConnected = false
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

  def onWhoRequest(channel: IrcChannel) = channel.onWho(this)

  def commandPrefix = s":${this.nickname}!${this.login}@${this.hostname} "

  def sendLine(line: String){}

  def modePrefix(channel: IrcChannel): String = {
    val modes = channel.getMode(this)
    if(modes.contains("q")) "~"
    else if(modes.contains("a")) "&"
    else if(modes.contains("o")) "@"
    else if(modes.contains("h")) "%"
    else if(modes.contains("v")) "+"
    else ""
  }

  final def setAllNames(name: String){
    this.nickname = name
    this.login = name
    this.realname = name
  }

  def noJoinNeeded = false
}
