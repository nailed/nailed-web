package jk_5.nailed.web.webserver.http.handlers

import io.netty.channel._
import io.netty.handler.codec.http._
import java.io.{FileNotFoundException, RandomAccessFile, File}
import io.netty.handler.stream.ChunkedFile
import jk_5.nailed.web.webserver.http.{HttpHeaderDateFormat, WebServerUtils}
import jk_5.nailed.web.webserver.{RoutedHandler, UrlEscaper}

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerHtml extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {

  private final val htdocs = System.getProperty("webserver.htdocslocation", "web")
  private final val htdocsLocation = if(htdocs.endsWith("/")) htdocs.substring(0,htdocs.length -1) else htdocs

  def channelRead0(ctx: ChannelHandlerContext, req: FullHttpRequest){
    val isHead = req.getMethod == HttpMethod.HEAD
    if(req.getMethod != HttpMethod.GET && !isHead){
      WebServerUtils.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED)
      return
    }
    val uri = this.getURLData.url.split("\\?", 2)(0)
    val path = htdocsLocation + UrlEscaper.sanitizeURI(uri)

    if(path == null){
      WebServerUtils.sendError(ctx, HttpResponseStatus.FORBIDDEN)
      return
    }
    var file = new File(path)
    if(file.isDirectory){
      val index = new File(file, "index.html")
      if(index.exists() && index.isFile) file = index
    }
    if(file.isHidden || !file.exists){
      WebServerUtils.sendError(ctx, HttpResponseStatus.NOT_FOUND)
      return
    }
    if(file.isDirectory){
      if(uri.endsWith("/")) {
        //this.sendFileList(ctx, file) //TODO: file list?
        WebServerUtils.sendError(ctx, HttpResponseStatus.NOT_FOUND)
      }else WebServerUtils.sendRedirect(ctx, uri + "/")
      return
    }
    if(!file.isFile){
      WebServerUtils.sendError(ctx, HttpResponseStatus.FORBIDDEN)
      return
    }
    val ifModifiedSince = req.headers().get(HttpHeaders.Names.IF_MODIFIED_SINCE)
    if(ifModifiedSince != null && !ifModifiedSince.isEmpty){
      val dateFormatter = HttpHeaderDateFormat.get
      val ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince)
      val ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime / 1000
      val fileLastModifiedSeconds = file.lastModified() / 1000
      if(ifModifiedSinceDateSeconds >= fileLastModifiedSeconds){
        WebServerUtils.sendNotModified(ctx, file)
        return
      }
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
    WebServerUtils.setDateAndCacheHeaders(response, file)

    ctx.write(response)
    if(!isHead) ctx.write(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)))
    ctx.write(LastHttpContent.EMPTY_LAST_CONTENT)
    ctx.flush()
  }
}
