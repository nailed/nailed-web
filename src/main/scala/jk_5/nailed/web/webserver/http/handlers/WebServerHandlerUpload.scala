package jk_5.nailed.web.webserver.http.handlers

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.RoutedHandler
import io.netty.handler.codec.http.multipart._
import jk_5.nailed.web.webserver.http.WebServerUtils
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.{EndOfDataDecoderException, ErrorDataDecoderException}
import java.io.File
import scala.collection.JavaConversions._

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerUpload extends SimpleChannelInboundHandler[HttpObject] with RoutedHandler {

  private val factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE)
  private var request: HttpRequest = null
  private var decoder: Option[HttpPostRequestDecoder] = None

  override def channelInactive(ctx: ChannelHandlerContext) = this.decoder.foreach(_.cleanFiles())

  override def channelRead0(ctx: ChannelHandlerContext, msg: HttpObject){
    msg match {
      case m: HttpRequest =>
        if(m.getMethod != HttpMethod.POST){
          WebServerUtils.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED)
          return
        }
        this.request = m
        try{
          this.decoder = Some(new HttpPostRequestDecoder(this.factory, m))
        }catch{
          case e: ErrorDataDecoderException =>
            this.logger.error(this.marker, s"Error while creating POST decoder for ${ctx.channel().remoteAddress()}", e)
            WebServerUtils.sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR)
        }
      case m: HttpContent =>
        if(decoder.isEmpty) {
          ctx.close() //FIXME: What should be sent to the client here?
          return
        }
        try{
          this.decoder.get.offer(m)
        }catch{
          case e: ErrorDataDecoderException =>
            this.logger.error(this.marker, s"Error while reading uploaded chunk for ${ctx.channel().remoteAddress()}", e)
            WebServerUtils.sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR)
        }
        this.readChunkedData()
        if(m.isInstanceOf[LastHttpContent]){
          //TODO: send response
          WebServerUtils.sendOK(ctx)

          decoder.get.getBodyHttpDatas.foreach {
            case f: FileUpload =>
              f.renameTo(new File(s"uploaded/${f.getFilename}"))
              this.logger.info(this.marker, s"${ctx.channel().remoteAddress().toString} uploaded ${f.getFilename}")
          }

          WebServerUtils.sendOK(ctx)
          decoder.get.destroy()

          this.request = null
          this.decoder.get.destroy()
          this.decoder = None
        }
    }
    /*if(msg.getMethod == HttpMethod.POST){
      val file = decoder.getBodyHttpData("file1").asInstanceOf[FileUpload]
      file.renameTo(new File(s"uploaded/${file.getFilename}"))
      WebServerUtils.sendOK(ctx)
      decoder.destroy()

      this.logger.info(this.marker, s"${ctx.channel().remoteAddress().toString} uploaded ${file.getFilename}")
    }*/
  }

  def readChunkedData(){
    try{
      while(this.decoder.get.hasNext){
        val data = this.decoder.get.next()
        if(data != null){
          try{
            //TODO: handle incoming chunks
            println("Incoming: " + data)
          }finally{
            data.release()
          }
        }
      }
    }catch{
      case e: EndOfDataDecoderException => //Done reading
    }
  }
}
