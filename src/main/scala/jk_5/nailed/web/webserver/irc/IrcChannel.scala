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

  def onJoin(connection: IrcConnection){
    var names = connection.nickname + " "
    this.connections.filter(_.nickname != connection.nickname).foreach(c => names += c.nickname + " ")
    names = names.trim
    connection.channel.write(s"${connection.commandPrefix}JOIN :$name")
    connection.channel.write(s":${ProtocolIrc.host} 332 ${connection.nickname} $name :$topic")
    connection.channel.write(s":${ProtocolIrc.host} 333 ${connection.nickname} $name $topicBy ${Math.round(this.topicTime.getTime / 1000)}")
    connection.channel.write(s":${ProtocolIrc.host} 353 ${connection.nickname} = $name :$names")
    connection.channel.write(s":${ProtocolIrc.host} 366 ${connection.nickname} $name :End of /NAMES list.")
    connection.channel.flush()
    if(this.connections.find(_.nickname == connection.nickname).isEmpty){
      //When this user doesn't have a client connected already, notify everyone
      this.connections.filter(_.nickname != connection.nickname).foreach(c => c.channel.writeAndFlush(s"${connection.commandPrefix}JOIN :$name"))
    }
    this.connections.add(connection)
  }

  def onPart(connection: IrcConnection){
    if(this.connections.contains(connection)){
      this.connections.remove(connection)
      connection.channel.writeAndFlush(s"${connection.commandPrefix}PART $name")
      if(this.connections.find(_.nickname == connection.nickname).isEmpty){
        //Only when the user doesn't have more clients connected, notify everyone
        this.connections.filter(_.nickname != connection.nickname).foreach(c => c.channel.writeAndFlush(s"${connection.commandPrefix}PART $name"))
      }
    }
  }

  def onPart(connection: IrcConnection, reason: String){
    if(this.connections.contains(connection)){
      this.connections.remove(connection)
      connection.channel.writeAndFlush(s"${connection.commandPrefix}PART $name :$reason")
      if(this.connections.find(_.nickname == connection.nickname).isEmpty){
        //Only when the user doesn't have more clients connected, notify everyone
        this.connections.filter(_.nickname != connection.nickname).foreach(c => c.channel.writeAndFlush(s"${connection.commandPrefix}PART $name :$reason"))
      }
    }
  }

  def onMessage(connection: IrcConnection, message: String){
    if(this.connections.contains(connection)){ //TODO: check +n for this
      this.connections.filter(_ != connection).foreach(c => c.channel.writeAndFlush(s"${connection.commandPrefix}PRIVMSG ${this.name} $message"))
    }
  }

  def onMode(connection: IrcConnection){
    if(this.connections.contains(connection)){
      connection.channel.write(s":${ProtocolIrc.host} 324 ${connection.nickname} $name +$mode")
      connection.channel.write(s":${ProtocolIrc.host} 329 ${connection.nickname} $name 0") //TODO: channel created time
      connection.channel.flush()
    }
  }

  def onTopic(connection: IrcConnection){
    if(this.connections.contains(connection)){
      connection.channel.write(s":${ProtocolIrc.host} 332 ${connection.nickname} $name :$topic")
      connection.channel.write(s":${ProtocolIrc.host} 333 ${connection.nickname} $name $topicBy ${topicTime.getTime}")
      connection.channel.flush()
    }
  }

  def onSetTopic(connection: IrcConnection, newTopic: String){
    if(this.connections.contains(connection)){ //TODO: check +t
      this.topic = newTopic
      this.topicBy = connection.nickname
      this.topicTime = new Date()
      this.connections.foreach(c => c.channel.writeAndFlush(s"${connection.commandPrefix}TOPIC $name :$newTopic"))
    }
  }
}
