package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.http.WebServerUtils
import jk_5.nailed.web.webserver.RoutedHandler
import java.io.{FileNotFoundException, RandomAccessFile, File}
import java.util.Date
import io.netty.handler.stream.ChunkedFile
import jk_5.nailed.web.webserver.http.HttpHeaderDateFormat
import jk_5.nailed.web.mappack.MappackFilestore

/**
 * No description given
 *
 * @author jk-5
 */
class ApiHandlerMappackFile extends SimpleChannelInboundHandler[HttpObject] with RoutedHandler {

  private var request: HttpRequest = null

  def channelRead0(ctx: ChannelHandlerContext, msg: HttpObject){
    msg match {
      case req: HttpRequest => this.request = req
      case _: LastHttpContent =>
        val isHead = request.getMethod == HttpMethod.HEAD
        if(request.getMethod != HttpMethod.GET && !isHead){
          WebServerUtils.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED)
          return
        }
        val hash = this.getURLData.parameters.get("part1").get
        val file = new File(new File(MappackFilestore.objectRoot, hash.substring(0, 2)), hash)

        if(file.isHidden || !file.exists){
          WebServerUtils.sendError(ctx, HttpResponseStatus.NOT_FOUND)
          return
        }
        var raf: RandomAccessFile = null
        try{
          raf = new RandomAccessFile(file, "r")
        }catch{
          case e: FileNotFoundException =>
            WebServerUtils.sendError(ctx, HttpResponseStatus.NOT_FOUND)
            return
        }
        val fileLength = raf.length()
        val response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)

        WebServerUtils.setContentLength(response, fileLength)
        WebServerUtils.setContentType(response, file)
        response.headers().set(HttpHeaders.Names.LAST_MODIFIED, HttpHeaderDateFormat.get.format(new Date(file.lastModified())))

        val ifModifiedSince = request.headers().get(HttpHeaders.Names.IF_MODIFIED_SINCE)
        if(ifModifiedSince != null && !ifModifiedSince.isEmpty){
          val dateFormatter = HttpHeaderDateFormat.get
          val ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince)
          val ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime / 1000
          val fileLastModifiedSeconds = file.lastModified() / 1000
          if(ifModifiedSinceDateSeconds >= fileLastModifiedSeconds){
            response.setStatus(HttpResponseStatus.NOT_MODIFIED)
            WebServerUtils.sendNotModified(ctx, file)
            return
          }
        }

        WebServerUtils.setDateAndCacheHeaders(response, file)

        ctx.write(response)
        if(!isHead) ctx.write(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)))
        ctx.write(LastHttpContent.EMPTY_LAST_CONTENT)
        ctx.flush()
      case _: HttpContent => //We should not get chunked requests here, but just to be safe
    }
  }
}
