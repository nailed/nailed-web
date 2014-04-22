package jk_5.nailed.web.webserver.irc

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

  def onWhoRequest(channel: IrcChannel) = channel.onWho(this)

  def commandPrefix = s":${this.nickname}!${this.login}@${this.hostname} "

  def sendLine(line: String){}

  def modes(channel: IrcChannel): String = channel.modes.get(this).getOrElse("")
  def setMode(channel: IrcChannel, mode: String){
    val modes = mode.substring(1)
    var current = this.modes(channel)
    var additions = ""
    if(mode.startsWith("+")){
      additions = "+"
      modes.split("").foreach(m => if(!current.contains(m)){current += m; additions += m})
    }else if(mode.startsWith("-")){
      additions = "-"
      modes.split("").foreach(m => if(current.contains(m)){current.replace(m, ""); additions += m})
    }
    channel.modes.put(this, current)
    if(additions.length > 1){
      channel.connections.foreach(_.sendLine(s":${ProtocolIrc.host}!server@${ProtocolIrc.host} MODE ${channel.name} $additions $nickname"))
    }
  }
  def modePrefix(channel: IrcChannel): String = {
    val modes = this.modes(channel)
    if(modes.contains("o")) "@"
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
