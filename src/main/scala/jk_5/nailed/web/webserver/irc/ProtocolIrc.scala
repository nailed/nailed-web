package jk_5.nailed.web.webserver.irc

import jk_5.nailed.web.webserver.ServerProtocol
import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelPromise, ChannelHandlerContext, ChannelDuplexHandler, Channel}
import io.netty.handler.codec.{Delimiters, DelimiterBasedFrameDecoder}
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}
import jk_5.nailed.web.webserver.irc.handler.{HandshakeHandler, OutboundFrameAppender}
import io.netty.util.AttributeKey
import org.apache.logging.log4j.{MarkerManager, LogManager}
import scala.collection.mutable
import jk_5.nailed.web.webserver.irc.connections.{ServerConnection, ServerBotConnection}

/**
 * No description given
 *
 * @author jk-5
 */
object ProtocolIrc extends ServerProtocol {

  val host = "nailed.jk-5.tk"
  val connection: AttributeKey[IrcConnection] = AttributeKey.valueOf("connection")
  val logger = LogManager.getLogger
  val marker = MarkerManager.getMarker("IRC")
  val channels = mutable.HashSet[IrcChannel]()
  val connections = mutable.HashSet[IrcConnection]()

  val channelPrefixes = Array("#")

  this.connections += ServerBotConnection
  this.connections += ServerConnection

  override def matches(buffer: ByteBuf): Boolean = {
    val c1 = buffer.getUnsignedByte(buffer.readerIndex())
    val c2 = buffer.getUnsignedByte(buffer.readerIndex() + 1)
    val c3 = buffer.getUnsignedByte(buffer.readerIndex() + 2)
    val c4 = buffer.getUnsignedByte(buffer.readerIndex() + 3)
    (c1 == 'N' && c2 == 'I' && c3 == 'C' && c4 == 'K') || (c1 == 'P' && c2 == 'A' && c3 == 'S' && c4 == 'S') || (c1 == 'C' && c2 == 'A' && c3 == 'P')
  }

  val encoder = new StringEncoder
  val decoder = new StringDecoder

  override def configureChannel(channel: Channel){
    val pipe = channel.pipeline()
    //pipe.addLast("logger", new LoggingHandler(LogLevel.INFO))
    pipe.addLast("framer", new DelimiterBasedFrameDecoder(510, Delimiters.lineDelimiter(): _*))
    pipe.addLast("stringEncoder", encoder)
    pipe.addLast("stringDecoder", decoder)
    pipe.addLast("outboundFramer", OutboundFrameAppender)
    pipe.addLast("logger", new ChannelDuplexHandler(){
      override def write(ctx: ChannelHandlerContext, msg: scala.Any, promise: ChannelPromise){
        logger.debug("Outbound: " + msg.toString)
        ctx.write(msg, promise)
      }
      override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any){
        logger.debug("Inbound: " + msg.toString)
        ctx.fireChannelRead(msg)
      }
    })
    pipe.addLast("handshakeHandler", new HandshakeHandler)
  }

  def getChannel(channel: String) = this.channels.find(_.name == channel)
  def getOrCreateChannel(channel: String): IrcChannel = this.channels.synchronized {
    var ch = this.getChannel(channel)
    if(ch.isEmpty){
      this.logger.trace(this.marker, s"Created new channel $channel")
      ch = Some(new IrcChannel(channel))
      this.channels.add(ch.get)
    }
    ch.get
  }

  def onConnect(conn: IrcConnection){
    this.connections += conn
  }

  def onDisconnect(conn: IrcConnection){
    this.connections -= conn
  }

  def getConnection(nickname: String) = this.connections.find(_.nickname == nickname)
  def getConnections(nickname: String) = this.connections.filter(_.nickname == nickname)

  def parseOperationAndArgs(frame: String): (String, Array[String]) = {
    val ind = frame.indexOf(' ')
    if(ind == -1){
      return (frame, new Array[String](0))
    }
    val operation = frame.substring(0, ind)
    val argsString = frame.substring(ind + 1)
    var end = false
    val argsBuilder = mutable.ArrayBuilder.make[String]()
    var spaceIndex = -1
    do {
      val old = spaceIndex
      spaceIndex = argsString.indexOf(' ', spaceIndex + 1)
      val a = if(spaceIndex == -1){
        end = true
        argsString.substring(old + 1)
      } else argsString.substring(old + 1, spaceIndex)
      if(a.startsWith(":")) {
        argsBuilder += argsString.substring(old + 2)
        end = true
      } else {
        argsBuilder += a
      }
    } while(!end)

    (operation, argsBuilder.result())
  }
}
