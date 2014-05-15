package jk_5.nailed.web.crash

import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.webserver.irc.{IrcFormatting, IrcConnection, ProtocolIrc}

/**
 * No description given
 *
 * @author jk-5
 */
object CrashHandler {

  val channel = ProtocolIrc.getOrCreateChannel("#crashes")
  val connection = new IrcConnection("CrashReporter")
  ProtocolIrc.onConnect(connection)
  connection join channel
  channel.setMode(connection, "+q")

  def addCrashReport(stacktrace: String, data: JsonObject){
    val lines = stacktrace.split("\r|\n|\r\n")
    val head = lines(0)
    connection.sendMessage(channel, IrcFormatting.RED + IrcFormatting.UNDERLINE + "Crash:" + IrcFormatting.NORMAL + IrcFormatting.DARK_BLUE + " " + head)
  }
}
