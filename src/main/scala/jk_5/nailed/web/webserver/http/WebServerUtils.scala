package jk_5.nailed.web.webserver.http

import java.util.{Date, Calendar}
import io.netty.channel.{ChannelFutureListener, ChannelFuture, ChannelHandlerContext}
import io.netty.handler.codec.http._
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import java.io.File
import jk_5.nailed.web.webserver.MimeTypesLookup
import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.auth.{AuthSession, SessionManager, UserDatabase}
import jk_5.nailed.web.couchdb.UID
import scala.collection.JavaConversions._
import io.netty.handler.codec.http.multipart.{HttpPostRequestDecoder, Attribute}

/**
 * No description given
 *
 * @author jk-5
 */
object WebServerUtils {

  final val HTTP_CACHE_SECONDS = 60

  def sendError(ctx: ChannelHandlerContext, status: HttpResponseStatus): ChannelFuture =
    this.sendJson(ctx, new JsonObject().add("status", "error").add("error", status.toString), status)
  def sendHeaders(ctx: ChannelHandlerContext, status: HttpResponseStatus): ChannelFuture = {
    val response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status)
    ctx.writeAndFlush(response)
  }
  def sendRedirect(ctx: ChannelHandlerContext, destination: String): ChannelFuture = {
    val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND)
    response.headers().set(HttpHeaders.Names.LOCATION, destination)
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
  }
  def sendNotModified(ctx: ChannelHandlerContext, file: File): ChannelFuture = {
    val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_MODIFIED)
    this.setContentType(response, file)
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
  }
  def setContentLength(response: HttpResponse, fileLength: Long) = HttpHeaders.setContentLength(response, fileLength)
  def setContentType(response: HttpResponse, file: File) = response.headers().set(HttpHeaders.Names.CONTENT_TYPE, MimeTypesLookup.getMimeType(file))
  def setDateAndCacheHeaders(response: HttpResponse, file: File){
    val time = Calendar.getInstance()
    time.add(Calendar.SECOND, this.HTTP_CACHE_SECONDS)
    response.headers().set(HttpHeaders.Names.EXPIRES, HttpHeaderDateFormat.get.format(time.getTime))
    response.headers().set(HttpHeaders.Names.CACHE_CONTROL, "private, max-age=" + this.HTTP_CACHE_SECONDS)
    response.headers().set(HttpHeaders.Names.LAST_MODIFIED, HttpHeaderDateFormat.get.format(new Date(file.lastModified())))
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
    if(cookieString != null){
      val cookies = CookieDecoder.decode(cookieString)
      val ses = cookies.find(_.getName == "sessid")
      val uid = cookies.find(_.getName == "uid")
      if(ses.isEmpty || uid.isEmpty){
        this.sendJson(ctx, new JsonObject().add("status", "error").add("error", "No session cookie was found"), HttpResponseStatus.UNAUTHORIZED)
        None
      }else{
        val user = UserDatabase.getUser(UID(uid.get.getValue))
        val session = SessionManager.getSession(ses.get.getValue)
        if(session.isEmpty || user.isEmpty){
          WebServerUtils.sendJson(ctx, new JsonObject().add("status", "error").add("error", "No session cookie was found"), HttpResponseStatus.UNAUTHORIZED)
          return None
        }
        if(session.get.getUserID != user.get.getID){
          WebServerUtils.sendJson(ctx, new JsonObject().add("status", "error").add("error", "That session does not belong to you"), HttpResponseStatus.UNAUTHORIZED)
          return None
        }
        Some(session.get)
      }
    }else{
      this.sendJson(ctx, new JsonObject().add("status", "error").add("error", "No session cookie was found"), HttpResponseStatus.UNAUTHORIZED)
      None
    }
  }
  def getPostEntry(data: HttpPostRequestDecoder, entry: String): Option[String] = {
    if(data.getBodyHttpData(entry) != null){
      Some(data.getBodyHttpData(entry).asInstanceOf[Attribute].getValue)
    }else None
  }
  def setCookie(response: HttpResponse, key: String, value: String){
    val cookie = new DefaultCookie(key, value)
    cookie.setPath("/")
    //response.headers().add(HttpHeaders.Names.SET_COOKIE, key + "=" + value + "; path=/")
    this.setCookie(response, cookie)
  }
  def setCookie(response: HttpResponse, cookie: Cookie){
    response.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode(cookie))
  }
}
