package jk_5.nailed.web.webserver.http

import java.util.{Date, Calendar}
import io.netty.channel.{ChannelFutureListener, ChannelFuture, ChannelHandlerContext}
import io.netty.handler.codec.http._
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import java.io.File
import jk_5.nailed.web.webserver.MimeTypesLookup
import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.auth.{AuthSession, SessionManager}
import jk_5.nailed.web.couchdb.UID
import scala.collection.JavaConversions._
import scala.collection.mutable
import io.netty.handler.codec.http.multipart.{HttpPostRequestDecoder, Attribute}
import org.apache.logging.log4j.LogManager

/**
 * No description given
 *
 * @author jk-5
 */
object WebServerUtils {

  final val HTTP_CACHE_SECONDS = 60
  private final val logger = LogManager.getLogger

  def sendError(ctx: ChannelHandlerContext, status: HttpResponseStatus): ChannelFuture = this.sendError(ctx, status.toString, status)

  def sendError(ctx: ChannelHandlerContext, message: String, status: HttpResponseStatus = HttpResponseStatus.OK): ChannelFuture =
    this.sendJson(ctx, new JsonObject().add("status", "error").add("error", message), status)

  def sendHeaders(ctx: ChannelHandlerContext, status: HttpResponseStatus): ChannelFuture = {
    val response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status)
    ctx.write(response)
  }

  def sendRedirect(ctx: ChannelHandlerContext, destination: String): ChannelFuture = {
    val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND)
    response.headers().set(HttpHeaders.Names.LOCATION, destination)
    ctx.writeAndFlush(response)
  }

  def sendNotModified(ctx: ChannelHandlerContext, configure: (HttpResponse) => Unit): ChannelFuture = {
    val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_MODIFIED)
    ctx.writeAndFlush(response)
  }
  def sendNotModified(ctx: ChannelHandlerContext, file: File): ChannelFuture = this.sendNotModified(ctx, r => this.setContentType(r, file))

  def setContentLength(response: HttpResponse, fileLength: Long) = HttpHeaders.setContentLength(response, fileLength)

  def setContentType(response: HttpResponse, file: File) = response.headers().set(HttpHeaders.Names.CONTENT_TYPE, MimeTypesLookup.getMimeType(file))
  def setContentType(response: HttpResponse, ext: String) = response.headers().set(HttpHeaders.Names.CONTENT_TYPE, MimeTypesLookup.getMimeTypeFromExt(ext))

  def setDateAndCacheHeaders(response: HttpResponse, file: File): Unit = this.setDateAndCacheHeaders(response, file.lastModified())
  def setDateAndCacheHeaders(response: HttpResponse, lastModified: Long){
    val time = Calendar.getInstance()
    time.add(Calendar.SECOND, this.HTTP_CACHE_SECONDS)
    response.headers().set(HttpHeaders.Names.EXPIRES, HttpHeaderDateFormat.get.format(time.getTime))
    response.headers().set(HttpHeaders.Names.CACHE_CONTROL, "private, max-age=" + this.HTTP_CACHE_SECONDS)
    response.headers().set(HttpHeaders.Names.LAST_MODIFIED, HttpHeaderDateFormat.get.format(new Date(lastModified)))
  }

  def jsonResponse(json: JsonObject, status: HttpResponseStatus = HttpResponseStatus.OK) =
    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(json.stringify, CharsetUtil.UTF_8))

  def sendJson(ctx: ChannelHandlerContext, json: JsonObject, status: HttpResponseStatus = HttpResponseStatus.OK) =
    ctx.writeAndFlush(this.jsonResponse(json, status))

  def removeCookie(resp: HttpResponse, name: String){
    val cookie = new DefaultCookie(name, "")
    cookie.setMaxAge(Long.MinValue + 1)
    cookie.setPath("/")
    cookie.setDiscard(true)
    this.setCookie(resp, cookie)
  }

  def checkSession(ctx: ChannelHandlerContext, req: HttpRequest): Option[AuthSession] = {
    val cookieString = req.headers().get(HttpHeaders.Names.COOKIE)
    var allow = false
    var session: AuthSession = null
    var userid: UID = null
    if(cookieString == null){
      val args = new QueryStringDecoder(req.getUri)
      if(args.parameters().containsKey("uid") && args.parameters().containsKey("sessid")){
        val u = args.parameters().get("uid").get(0)
        val s = args.parameters().get("sessid").get(0)
        val sess = SessionManager.getSession(s)
        if(sess.isDefined && sess.get.getUserID.toString == u){
          session = sess.get
          userid = UID(u)
          allow = true
        }
      }
    }else{
      val cookies = CookieDecoder.decode(cookieString)
      val ids = mutable.ArrayBuffer[Cookie]()
      val sessions = mutable.ArrayBuffer[Cookie]()
      cookies.foreach(c => {
        if(c.getName.startsWith("uid")){
          ids += c
        }else if(c.getName.startsWith("sessid")){
          sessions += c
        }
      })
      sessions.foreach(c => {
        val r = c.getName.substring(6)
        val uid = ids.find(_.getName == "uid" + r)
        if(uid.isDefined){
          val sess = SessionManager.getSession(c.getValue)
          if(sess.isDefined && sess.get.getUserID.toString == uid.get.getValue){
            session = sess.get
            userid = UID(uid.get.getValue)
            allow = true
          }
        }
      })
    }
    if(!allow){
      this.sendJson(ctx, new JsonObject().add("status", "error").add("error", "No valid session was found"), HttpResponseStatus.UNAUTHORIZED)
      None
    }else Some(session)
  }

  def getPostEntry(data: HttpPostRequestDecoder, entry: String): Option[String] = {
    if(data.getBodyHttpData(entry) != null){
      Some(data.getBodyHttpData(entry).asInstanceOf[Attribute].getValue)
    }else None
  }

  def setCookie(response: HttpResponse, key: String, value: String){
    val cookie = new DefaultCookie(key, value)
    cookie.setPath("/")
    this.setCookie(response, cookie)
  }

  def setCookie(response: HttpResponse, cookie: Cookie){
    response.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode(cookie))
  }

  def setSession(response: HttpResponse, session: AuthSession){
    val rand = getRandomFromUid(session.getID)
    this.setCookie(response, "uid" + rand, session.getUser.getID.toString)
    this.setCookie(response, "sessid" + rand, session.getID.toString)
  }

  def removeSession(response: HttpResponse, session: AuthSession){
    val rand = getRandomFromUid(session.getID)
    this.removeCookie(response, "uid" + rand)
    this.removeCookie(response, "sessid" + rand)
  }

  def getRandomFromUid(uid: UID): String = {
    val id = uid.toString
    new mutable.StringBuilder() append id.charAt(2) append id.charAt(4) append id.charAt(6) append id.charAt(8) append id.charAt(10) append id.charAt(12) toString()
  }

  def sendOK(ctx: ChannelHandlerContext, data: JsonObject = new JsonObject): ChannelFuture = this.sendJson(ctx, data.add("status", "ok"))

  def okResponse(json: JsonObject = new JsonObject, status: HttpResponseStatus = HttpResponseStatus.OK) =
    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(json.add("status", "ok").stringify, CharsetUtil.UTF_8))

  def closeIfRequested(req: HttpRequest, future: ChannelFuture): Boolean =
    if(!HttpHeaders.isKeepAlive(req)){
      future.addListener(ChannelFutureListener.CLOSE)
      true
    }else false
}
