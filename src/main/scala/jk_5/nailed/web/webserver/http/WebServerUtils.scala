package jk_5.nailed.web.webserver.http

import java.text.SimpleDateFormat
import java.util.{Date, Calendar, TimeZone, Locale}
import io.netty.channel.{ChannelFutureListener, ChannelFuture, ChannelHandlerContext}
import io.netty.handler.codec.http._
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import java.io.File
import jk_5.nailed.web.webserver.MimeTypesLookup
import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
object WebServerUtils {

  final val HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz"
  final val HTTP_DATE_GMT_TIMEZONE = "GMT"
  final val HTTP_CACHE_SECONDS = 60
  final val formatter = new SimpleDateFormat(this.HTTP_DATE_FORMAT, Locale.US)
  formatter.setTimeZone(TimeZone.getTimeZone(this.HTTP_DATE_GMT_TIMEZONE))

  def sendError(ctx: ChannelHandlerContext, status: HttpResponseStatus): ChannelFuture = {
    val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(new JsonObject().add("error", status.toString).stringify, CharsetUtil.UTF_8))
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
  }
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
    response.headers().set(HttpHeaders.Names.EXPIRES, formatter.format(time.getTime))
    response.headers().set(HttpHeaders.Names.CACHE_CONTROL, "private, max-age=" + this.HTTP_CACHE_SECONDS)
    response.headers().set(HttpHeaders.Names.LAST_MODIFIED, formatter.format(new Date(file.lastModified())))
  }
  def sendJson(ctx: ChannelHandlerContext, json: JsonObject, status: HttpResponseStatus = HttpResponseStatus.OK){
    ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(json.stringify, CharsetUtil.UTF_8)))
  }
}
