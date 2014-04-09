package jk_5.nailed.web.webserver.irc

import jk_5.nailed.web.webserver.MultiplexedProtocol
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.handler.codec.{Delimiters, DelimiterBasedFrameDecoder}
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}
import java.util.concurrent.TimeUnit
import jk_5.nailed.web.webserver.irc.handler.{OutboundFrameAppender, PingHandler}

/**
 * No description given
 *
 * @author jk-5
 */
object ProtocolIrc extends MultiplexedProtocol {

  val host = "nailed.jk-5.tk"

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
  }
}
