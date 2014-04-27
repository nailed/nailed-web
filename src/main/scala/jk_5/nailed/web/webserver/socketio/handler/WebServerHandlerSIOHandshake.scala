package jk_5.nailed.web.webserver.socketio.handler

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.{MimeTypesLookup, RoutedHandler}
import jk_5.nailed.web.webserver.http.WebServerUtils
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import java.util.concurrent.TimeUnit
import scala.collection.mutable
import java.util.UUID
import io.netty.util.concurrent.ScheduledFuture
import jk_5.nailed.web.auth.AuthSession
import jk_5.nailed.web.webserver.socketio.HeartbeatHandler
import jk_5.nailed.web.webserver.http.apihandlers.{Responder, JsonHandler}

/**
 * No description given
 *
 * @author jk-5
 */
object WebServerHandlerSIOHandshake {
  val closeTimeout = 60

  val futures = mutable.HashMap[UUID, ScheduledFuture[_]]()
  val sessions = mutable.HashMap[UUID, AuthSession]()
}

class WebServerHandlerSIOHandshake extends JsonHandler with RoutedHandler {

  override def handlePOST(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder){
    val session = WebServerUtils.checkSession(ctx, msg)
    if(session.isDefined){
      val uid = UUID.randomUUID()
      val decoder = new QueryStringDecoder(msg.getUri)
      var jsonp: Option[String] = None
      if(decoder.parameters().containsKey("jsonp")){
        jsonp = Some(decoder.parameters().get("jsonp").get(0))
      }
      logger.trace(marker, s"User ${session.get.getUser.fullName} authorized for Socket.IO with id ${uid.toString}")
      val resdata = s"${uid.toString}:${HeartbeatHandler.heartbeatTimeout}:${WebServerHandlerSIOHandshake.closeTimeout}:websocket,flashsocket" //,flashsocket,xhr-polling
      val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(this.wrapJsonP(resdata, jsonp), CharsetUtil.UTF_8))
      response.headers().set(HttpHeaders.Names.CONTENT_TYPE, MimeTypesLookup.getMimeTypeFromExt("txt"))
      val f = ctx.writeAndFlush(response)
      WebServerUtils.closeIfRequested(msg, f)

      val future = ctx.channel().eventLoop().schedule(new Runnable {
        override def run(){
          WebServerHandlerSIOHandshake.futures.remove(uid)
          WebServerHandlerSIOHandshake.sessions.remove(uid)
          logger.trace(marker, s"Session ${uid.toString} for ${WebServerUtils.ipFor(ctx.channel(), msg)} timed out")
        }
      }, WebServerHandlerSIOHandshake.closeTimeout, TimeUnit.SECONDS)

      WebServerHandlerSIOHandshake.futures.put(uid, future)
      WebServerHandlerSIOHandshake.sessions.put(uid, session.get)
    }
  }

  def wrapJsonP(data: String, jsonp: Option[String]) = if(jsonp.isEmpty) data else "io.j[" + jsonp.get + "](\"" + data + "\")"
}
