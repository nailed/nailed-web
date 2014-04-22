package jk_5.nailed.web.webserver.irc

import scala.collection.mutable
import java.util.Date

/**
 * No description given
 *
 * @author jk-5
 */
class IrcChannel(val name: String) {

  val connections = mutable.HashSet[IrcConnection]()
  var topic = "No topic set"
  var topicBy = ProtocolIrc.host
  var topicTime = new Date
  var mode = "snt"
  val modes = mutable.HashMap[IrcConnection, String]()

  def onJoin(connection: IrcConnection){
    connection match {
      case a: AuthenticatedConnection =>
        if(a.getUser.permissions.ircOperator){
          a.setMode(this, "+o")
        }
      case _ =>
    }

    var names = connection.modePrefix(this) + connection.nickname + " "
    this.connections.filter(_.nickname != connection.nickname).foreach(c => names += c.modePrefix(this) + c.nickname + " ")
    names = names.trim
    connection.sendLine(s"${connection.commandPrefix}JOIN :$name")
    connection.sendLine(s":${ProtocolIrc.host} 332 ${connection.nickname} $name :$topic")
    connection.sendLine(s":${ProtocolIrc.host} 333 ${connection.nickname} $name $topicBy ${Math.round(this.topicTime.getTime / 1000)}")
    connection.sendLine(s":${ProtocolIrc.host} 353 ${connection.nickname} = $name :$names")
    connection.sendLine(s":${ProtocolIrc.host} 366 ${connection.nickname} $name :End of /NAMES list.")
    if(this.connections.find(_.nickname == connection.nickname).isEmpty){ //Already connected?
      this.connections.filter(_.nickname != connection.nickname).foreach(c => c.onUserJoinedChannel(connection, this))
    }
    this.connections.add(connection)

    connection match {
      case a: AuthenticatedConnection =>
        Option(a.getUser).foreach(u => {
          if(u.permissions.ircOperator){
            this.connections.foreach(_.sendLine(s":${ProtocolIrc.host}!server@${ProtocolIrc.host} MODE $name +o ${a.getUser.getUsername}"))
          }
        })
      case _ =>
    }
  }

  def onPart(connection: IrcConnection){
    if(this.connections.contains(connection)){
      this.connections.remove(connection)
      connection.sendLine(s"${connection.commandPrefix}PART $name")
      if(this.connections.find(_.nickname == connection.nickname).isEmpty){ //No more connections?
        this.connections.filter(_.nickname != connection.nickname).foreach(c => c.sendLine(s"${connection.commandPrefix}PART $name"))
      }
    }
  }

  def onPart(connection: IrcConnection, reason: String){
    if(this.connections.contains(connection)){
      this.connections.remove(connection)
      connection.sendLine(s"${connection.commandPrefix}PART $name :$reason")
      if(this.connections.find(_.nickname == connection.nickname).isEmpty){ //No more connections?
        this.connections.filter(_.nickname != connection.nickname).foreach(c => c.sendLine(s"${connection.commandPrefix}PART $name :$reason"))
      }
    }
  }

  def onMessage(connection: IrcConnection, message: String){
    if(this.connections.contains(connection) || connection.noJoinNeeded){ //TODO: check +n for this
      this.connections.filter(_ != connection).foreach(_.onChannelMessage(connection, this, message))
    }
  }

  def onMode(connection: IrcConnection){
    if(this.connections.contains(connection)){
      connection.sendLine(s":${ProtocolIrc.host} 324 ${connection.nickname} $name +$mode")
      connection.sendLine(s":${ProtocolIrc.host} 329 ${connection.nickname} $name 0") //TODO: channel created time
    }
  }

  def onTopicRequest(connection: IrcConnection){
    if(this.connections.contains(connection)){
      connection.sendLine(s":${ProtocolIrc.host} 332 ${connection.nickname} $name :$topic")
      connection.sendLine(s":${ProtocolIrc.host} 333 ${connection.nickname} $name $topicBy ${topicTime.getTime}")
    }
  }

  def onSetTopic(connection: IrcConnection, newTopic: String){
    if(this.connections.contains(connection)){ //TODO: check +t
      this.topic = newTopic
      this.topicBy = connection.nickname
      this.topicTime = new Date()
      this.connections.foreach(c => c.sendLine(s"${connection.commandPrefix}TOPIC $name :$newTopic"))
    }
  }

  def onWho(connection: IrcConnection){
    for(c <- this.connections){
      connection.sendLine(s":${ProtocolIrc.host} 352 ${connection.nickname} $name ${c.login} ${c.hostname} ${ProtocolIrc.host} ${c.nickname} H${c.modePrefix(this)} :0 ${c.realname}}")
    }
    connection.sendLine(s":${ProtocolIrc.host} 352 ${connection.nickname} $name :End of /WHO list.")
  }
}
