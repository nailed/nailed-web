package jk_5.nailed.web.webserver.http.response

import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.channel.ChannelHandlerContext
import java.util
import jk_5.nailed.web.webserver.http.{HttpHeaderDateFormat, WebServerUtils, HttpHeaderAppender}
import io.netty.handler.codec.http._
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import io.netty.channel.ChannelHandler.Sharable
import java.io.{FileNotFoundException, RandomAccessFile, File}
import java.util.Date
import io.netty.handler.stream.ChunkedFile
import jk_5.nailed.web.webserver.http.handlers.WebServerHandlerHtml

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object ResponseEncoder extends MessageToMessageEncoder[ErrorResponse] {

  override def encode(ctx: ChannelHandlerContext, msg: ErrorResponse, out: util.List[AnyRef]){
    val request = ctx.channel().attr(HttpHeaderAppender.request).get()
    val accept = Option(request.headers.get(HttpHeaders.Names.ACCEPT))
    if(accept.isDefined && accept.get.contains("text/html")){
      val isHead = request.getMethod == HttpMethod.HEAD
      if(request.getMethod != HttpMethod.GET && !isHead){
        WebServerUtils.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED)
        return
      }
      val file = new File(new File(WebServerHandlerHtml.htdocsLocation, "error"), msg.response.code() + ".html")
      if(file.isHidden || !file.exists || !file.isFile){
        out.add(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, msg.response, Unpooled.copiedBuffer(msg.json.stringify, CharsetUtil.UTF_8)))
        return
      }
      var raf: RandomAccessFile = null
      try{
        raf = new RandomAccessFile(file, "r")
      }catch{
        case e: FileNotFoundException =>
          out.add(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, msg.response, Unpooled.copiedBuffer(msg.json.stringify, CharsetUtil.UTF_8)))
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

      out.add(response)
      if(!isHead) out.add(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)))
      out.add(LastHttpContent.EMPTY_LAST_CONTENT)
    }else{
      out.add(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, msg.response, Unpooled.copiedBuffer(msg.json.stringify, CharsetUtil.UTF_8)))
    }
  }
}
