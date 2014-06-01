package jk_5.nailed.web.webserver.http

import jk_5.nailed.web.webserver.{RouterHandler, MultiplexedProtocol}
import io.netty.channel._
import io.netty.handler.codec.http._
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.buffer.{Unpooled, ByteBuf}
import jk_5.nailed.web.webserver.http.handlers._
import jk_5.nailed.web.webserver.socketio.handler.{WebServerHandlerSIOHandshake, WebServerHandlerFlashResources}
import jk_5.nailed.web.webserver.socketio.transport.websocket.WebServerHandlerSIOWebSocket
import jk_5.nailed.web.webserver.http.apihandlers._
import jk_5.nailed.web.crash.CrashHandler
import jk_5.nailed.web.webserver.http.response.ResponseEncoder
import io.netty.util.CharsetUtil

/**
 * No description given
 *
 * @author jk-5
 */
object ProtocolHttp extends MultiplexedProtocol {

  val webserverMultiplexer = new MultiplexingUrlResolver

  this.webserverMultiplexer.addHandler("/api/mappacks.json", classOf[ApiHandlerMappackList])
  this.webserverMultiplexer.addHandler("/api/mappacks/(.*).json", classOf[ApiHandlerMappackData])
  this.webserverMultiplexer.addHandler("/api/login/", classOf[ApiHandlerLogin])
  this.webserverMultiplexer.addHandler("/api/register/", classOf[ApiHandlerRegister])
  this.webserverMultiplexer.addHandler("/api/link/", classOf[ApiHandlerLinkMojang])
  this.webserverMultiplexer.addHandler("/api/servers.json", classOf[ApiHandlerServerList])
  this.webserverMultiplexer.addHandler("/api/activateAccount/(.*)/", classOf[ApiHandlerActivateAccount])
  this.webserverMultiplexer.addHandler("/api/createMappack/", classOf[ApiHandlerCreateMappack])
  this.webserverMultiplexer.addHandler("/api/loadMappack/", classOf[ApiHandlerLoadMappack])
  this.webserverMultiplexer.addHandler("/api/reportCrash/", classOf[ApiHandlerCrashReport])
  this.webserverMultiplexer.addHandler("/api/data/([0-9a-f]+)/", classOf[ApiHandlerMappackFile])
  this.webserverMultiplexer.addHandler("/socket.io/static/flashsocket/(.*).swf", classOf[WebServerHandlerFlashResources])
  this.webserverMultiplexer.addHandler("/socket.io/([0-9]+)/websocket/([0-9a-z]+)", classOf[WebServerHandlerSIOWebSocket])
  this.webserverMultiplexer.addHandler("/socket.io/([0-9]+)/flashsocket/([0-9a-z]+)", classOf[WebServerHandlerSIOWebSocket])
  this.webserverMultiplexer.addHandler("/socket.io/([0-9]+)/", classOf[WebServerHandlerSIOHandshake])
  this.webserverMultiplexer.addHandler("/(.*)", classOf[WebServerHandlerHtml])

  val router = new RouterHandler(this.webserverMultiplexer, "routedHandler")

  HttpRequestLogger //Init the requestlogger so it creates the channel right away instead of waiting for the first request
  CrashHandler //Init the crashhandler so it creates the channel right away instead of waiting for the first request

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

  val res = Unpooled.copiedBuffer("data", CharsetUtil.UTF_8)

  def configureChannel(channel: Channel){
    val pipe = channel.pipeline()
    println("Initializing http channel")
    pipe.addLast("httpDecoder", new HttpRequestDecoder)
    pipe.addLast("httpEncoder", new HttpResponseEncoder)
    pipe.addLast("compressor", new HttpContentCompressor)
    //pipe.addLast("aggregator", new HttpObjectAggregator(1024))
    pipe.addLast("requestLogger", HttpRequestLogger)
    pipe.addLast("httpHeaderAppender", HttpHeaderAppender)
    pipe.addLast("chunkedWriter", new ChunkedWriteHandler)
    /*pipe.addLast("simpleHttpHandler", new SimpleChannelInboundHandler[FullHttpRequest]{
      override def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest){
        println("Incoming request")
        val r = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(res))
        r.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
        ctx.writeAndFlush(res).addListener(new ChannelFutureListener {
          override def operationComplete(future: ChannelFuture) = println("Response sent")
        })
      }
    })*/
    pipe.addLast("responseEncoder", ResponseEncoder)
    pipe.addLast("webserverRouter", router)
    pipe.addLast("routedHandler", NotFoundHandler)
    pipe.addLast("httpExceptionHandler", HttpExceptionHandler)
  }
}
