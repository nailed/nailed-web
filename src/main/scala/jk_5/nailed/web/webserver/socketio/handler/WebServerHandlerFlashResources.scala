package jk_5.nailed.web.webserver.socketio.handler

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.{MimeTypesLookup, RoutedHandler}
import java.net.URL
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.handler.codec.http.HttpVersion._
import io.netty.handler.stream.ChunkedStream
import scala.collection.mutable
import org.apache.logging.log4j.{MarkerManager, LogManager}
import jk_5.nailed.web.webserver.http.{WebServerUtils, HttpHeaderDateFormat}

/**
 * No description given
 *
 * @author jk-5
 */
object WebServerHandlerFlashResources {
  private val resources = mutable.HashMap[String, URL]()
  val logger = LogManager.getLogger
  val marker = MarkerManager.getMarker(this.getClass.getSimpleName)

  def addResource(file: String, path: String){
    val resUrl = getClass.getResource(path)
    if(resUrl == null) {
      this.logger.error(this.marker, s"The specified resource was not found: $path")
      return
    }
    resources.put(file, resUrl)
  }

  this.addResource("WebSocketMain", "/static/WebSocketMain.swf")
  this.addResource("WebSocketMainInsecure", "/static/WebSocketMainInsecure.swf")
}

class WebServerHandlerFlashResources extends SimpleChannelInboundHandler[HttpObject] with RoutedHandler {

  private var request: HttpRequest = null

  override def channelRead0(ctx: ChannelHandlerContext, msg: HttpObject){
    msg match {
      case req: HttpRequest => this.request = req
      case _: LastHttpContent =>
        val file = this.getURLData.parameters.get("part1").get
        val url = WebServerHandlerFlashResources.resources.get(file)
        if(url.isDefined){
          val fileUrl = url.get.openConnection
          val lastModified = fileUrl.getLastModified
          val ifModifiedSince = request.headers().get(HttpHeaders.Names.IF_MODIFIED_SINCE)
          if(ifModifiedSince != null && !ifModifiedSince.isEmpty){
            val dateFormatter = HttpHeaderDateFormat.get
            val ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince)
            val ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime / 1000
            val fileLastModifiedSeconds = lastModified / 1000
            if(ifModifiedSinceDateSeconds >= fileLastModifiedSeconds){
              WebServerUtils.sendNotModified(ctx, _.headers().set(HttpHeaders.Names.CONTENT_TYPE, MimeTypesLookup.getMimeTypeFromExt("swf")))
              return
            }
          }
          val is = fileUrl.getInputStream
          if(is == null) {
            WebServerUtils.sendError(ctx, NOT_FOUND)
            return
          }
          val res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK)
          WebServerUtils.setContentLength(res, fileUrl.getContentLength)
          WebServerUtils.setContentType(res, "swf")
          WebServerUtils.setDateAndCacheHeaders(res, lastModified)

          ctx.write(res)
          if(this.request.getMethod != HttpMethod.HEAD) ctx.write(new ChunkedStream(is, fileUrl.getContentLength))
          ctx.write(LastHttpContent.EMPTY_LAST_CONTENT)
          ctx.flush()
        }
      case _: HttpContent => //We should not get chunked requests here, but just to be safe
    }
  }
}
