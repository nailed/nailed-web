package jk_5.nailed.web.webserver.http

import jk_5.nailed.web.webserver.{Pipeline, MultiplexedProtocol}
import io.netty.channel.Channel
import io.netty.handler.codec.http.{HttpContentCompressor, HttpObjectAggregator, HttpResponseEncoder, HttpRequestDecoder}
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.buffer.ByteBuf
import io.netty.handler.logging.LogLevel

/**
 * No description given
 *
 * @author jk-5
 */
object ProtocolHttp extends MultiplexedProtocol {

  def matches(buffer: ByteBuf): Boolean = {
    val byte1 = buffer.getUnsignedByte(buffer.readerIndex())
    val byte2 = buffer.getUnsignedByte(buffer.readerIndex() + 1)
    byte1 == 'G' && byte2 == 'E' || // GET
    byte1 == 'P' && byte2 == 'O' || // POST
    byte1 == 'P' && byte2 == 'U' || // PUT
    byte1 == 'H' && byte2 == 'E' || // HEAD
    byte1 == 'O' && byte2 == 'P' || // OPTIONS
    byte1 == 'P' && byte2 == 'A' || // PATCH
    byte1 == 'D' && byte2 == 'E' || // DELETE
    byte1 == 'T' && byte2 == 'R' || // TRACE
    byte1 == 'C' && byte2 == 'O'    // CONNECT
  }

  def configureChannel(channel: Channel){
    val pipe = channel.pipeline()
    pipe.addLast("httpDecoder", new HttpRequestDecoder)
    pipe.addLast("httpEncoder", new HttpResponseEncoder)
    pipe.addLast("compressor", new HttpContentCompressor(6))
    pipe.addLast("httpHeaderAppender", HttpHeaderAppender)
    pipe.addLast("aggregator", new HttpObjectAggregator(1048576))
    pipe.addLast("chunkedWriter", new ChunkedWriteHandler())
    pipe.addLast("webserverRouter", Pipeline.router)
    pipe.addLast("routedHandler", NotFoundHandler)
  }
}
