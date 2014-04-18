package jk_5.nailed.web.webserver.irc

import jk_5.nailed.web.webserver.MultiplexedProtocol
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.handler.codec.{Delimiters, DelimiterBasedFrameDecoder}
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}
import java.util.concurrent.TimeUnit
import jk_5.nailed.web.webserver.irc.handler.{HandshakeHandler, OutboundFrameAppender, PingHandler}
import io.netty.util.AttributeKey
import org.apache.logging.log4j.{MarkerManager, LogManager}
import scala.collection.mutable

/**
 * No description given
 *
 * @author jk-5
 */
object ProtocolIrc extends MultiplexedProtocol {

  val host = "nailed.jk-5.tk"
  val connection: AttributeKey[IrcConnection] = AttributeKey.valueOf("connection")
  val logger = LogManager.getLogger
  val marker = MarkerManager.getMarker("IRC")
  val channels = mutable.HashSet[IrcChannel]()

  override def matches(buffer: ByteBuf): Boolean = {
    val c1 = buffer.readUnsignedByte()
    val c2 = buffer.readUnsignedByte()
    val c3 = buffer.readUnsignedByte()
    val c4 = buffer.readUnsignedByte()
    (c1 == 'N' && c2 == 'I' && c3 == 'C' && c4 == 'K') || (c1 == 'P' && c2 == 'A' && c3 == 'S' && c4 == 'S')
  }

  val encoder = new StringEncoder
  val decoder = new StringDecoder

  override def configureChannel(channel: Channel){
    val pipe = channel.pipeline()
    //pipe.addLast("logger", new LoggingHandler(LogLevel.INFO))
    pipe.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter(): _*))
    pipe.addLast("stringEncoder", encoder)
    pipe.addLast("stringDecoder", decoder)
    pipe.addLast("outboundFramer", OutboundFrameAppender)
    pipe.addLast("pingHandler", new PingHandler(1, TimeUnit.MINUTES))
    pipe.addLast("handshakeHandler", new HandshakeHandler)
  }

  def getChannel(channel: String) = this.channels.find(_.name == channel)
  def getOrCreateChannel(channel: String): IrcChannel = {
    var ch = this.getChannel(channel)
    if(ch.isEmpty){
      this.logger.trace(this.marker, s"Created new channel $channel")
      ch = Some(new IrcChannel(channel))
      this.channels.add(ch.get)
    }
    ch.get
  }
}
