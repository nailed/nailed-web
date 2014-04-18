package jk_5.nailed.web.webserver.http

import jk_5.nailed.web.webserver.{RouterHandler, MultiplexedProtocol}
import io.netty.channel.Channel
import io.netty.handler.codec.http.{HttpContentCompressor, HttpObjectAggregator, HttpResponseEncoder, HttpRequestDecoder}
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.buffer.ByteBuf
import jk_5.nailed.web.webserver.http.handlers._
import jk_5.nailed.web.webserver.socketio.handler.{WebServerHandlerSIOHandshake, WebServerHandlerFlashResources}
import jk_5.nailed.web.webserver.socketio.transport.websocket.WebServerHandlerSIOWebSocket

/**
 * No description given
 *
 * @author jk-5
 */
object ProtocolHttp extends MultiplexedProtocol {

  val webserverMultiplexer = new MultiplexingUrlResolver

  this.webserverMultiplexer.addHandler("/api/mappacks.json", classOf[WebServerHandlerMappackList])
  this.webserverMultiplexer.addHandler("/api/mappacks/(.*).json", classOf[WebServerHandlerMappackData])
  this.webserverMultiplexer.addHandler("/api/login/", classOf[WebServerHandlerLogin])
  this.webserverMultiplexer.addHandler("/api/register/", classOf[WebServerHandlerRegister])
  this.webserverMultiplexer.addHandler("/api/link/", classOf[WebServerHandlerLinkMojang])
  this.webserverMultiplexer.addHandler("/api/servers.json", classOf[WebServerHandlerServerList])
  this.webserverMultiplexer.addHandler("/upload/", classOf[WebServerHandlerUpload])
  this.webserverMultiplexer.addHandler("/socket.io/static/flashsocket/(.*).swf", classOf[WebServerHandlerFlashResources])
  this.webserverMultiplexer.addHandler("/socket.io/([0-9]+)/websocket/([0-9a-z]+)", classOf[WebServerHandlerSIOWebSocket])
  this.webserverMultiplexer.addHandler("/socket.io/([0-9]+)/flashsocket/([0-9a-z]+)", classOf[WebServerHandlerSIOWebSocket])
  this.webserverMultiplexer.addHandler("/socket.io/([0-9]+)/", classOf[WebServerHandlerSIOHandshake])
  this.webserverMultiplexer.addHandler("/(.*)", classOf[WebServerHandlerHtml])

  val router = new RouterHandler(this.webserverMultiplexer, "routedHandler")

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
    pipe.addLast("aggregator", new HttpObjectAggregator(1048576))
    pipe.addLast("httpHeaderAppender", HttpHeaderAppender)
    pipe.addLast("chunkedWriter", new ChunkedWriteHandler())
    pipe.addLast("webserverRouter", router)
    pipe.addLast("routedHandler", NotFoundHandler)
  }
}
