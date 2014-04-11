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

  def onJoin(connection: IrcConnection){
    var names = connection.nickname + " "
    this.connections.filter(_ != connection).foreach(c => names += c.nickname + " ")
    names = names.trim
    connection.channel.write(s"${connection.commandPrefix}JOIN :$name")
    connection.channel.write(s":${ProtocolIrc.host} 332 ${connection.nickname} $name :$topic")
    connection.channel.write(s":${ProtocolIrc.host} 333 ${connection.nickname} $name $topicBy ${Math.round(this.topicTime.getTime / 1000)}")
    connection.channel.write(s":${ProtocolIrc.host} 353 ${connection.nickname} = $name :$names")
    connection.channel.write(s":${ProtocolIrc.host} 366 ${connection.nickname} $name :End of /NAMES list.")
    connection.channel.flush()
    this.connections.foreach(c => c.channel.writeAndFlush(s"${connection.commandPrefix}JOIN :$name"))
    this.connections.add(connection)
  }
}
