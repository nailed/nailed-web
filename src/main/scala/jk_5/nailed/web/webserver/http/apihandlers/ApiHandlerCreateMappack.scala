package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.RoutedHandler
import io.netty.handler.codec.http.multipart._
import jk_5.nailed.web.webserver.http.WebServerUtils
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.{EndOfDataDecoderException, ErrorDataDecoderException}
import jk_5.nailed.web.mappack.{MappackRegistry, Mappack, MappackBuildCallback, MappackBuilder}
import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
class ApiHandlerCreateMappack extends SimpleChannelInboundHandler[HttpObject] with RoutedHandler {

  private val factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE)
  private var request: HttpRequest = null
  private var decoder: Option[HttpPostRequestDecoder] = None
  private var builder: Option[MappackBuilder] = None

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
            this.logger.error(this.marker, s"Error while creating POST decoder for ${WebServerUtils.ipFor(ctx.channel(), m)}", e)
            WebServerUtils.sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR)
            return
        }
      case m: HttpContent =>
        if(decoder.isEmpty) {
          WebServerUtils.sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR)
          return
        }
        try{
          this.decoder.get.offer(m)
        }catch{
          case e: ErrorDataDecoderException =>
            this.logger.error(this.marker, s"Error while reading uploaded chunk for ${WebServerUtils.ipFor(ctx.channel(), this.request)}", e)
            WebServerUtils.sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR)
            return
        }
        this.readChunkedData()
        if(m.isInstanceOf[LastHttpContent]){
          val rpd = new Responder(ctx.channel())
          builder.get.build(new MappackBuildCallback {
            override def onError(description: String) = rpd.error(description)
            override def onDone(mappack: Mappack) = {
              rpd.ok
              MappackRegistry.addMappack(mappack)
            }
          })

          this.request = null
          this.decoder.get.destroy()
          this.decoder = None
        }
    }
  }

  def readChunkedData(){
    try{
      while(this.decoder.get.hasNext){
        val data = this.decoder.get.next()
        val builder = this.builder.getOrElse(new MappackBuilder)
        if(this.builder.isEmpty) this.builder = Some(builder)
        if(data != null){
          try{
            data match {
              case f: FileUpload => builder.setMapFile(f)
              case a: Attribute => a.getName match {
                case "id" => builder.setId(a.getString)
                case "name" => builder.setName(a.getString)
                case "worldType" => builder.setWorldType(a.getString)
                case "spawns" => builder.setSpawns(JsonObject.readFrom(a.getValue))
                case "worldSource" => builder.setWorldSource(a.getString)
                case "gamemode" => builder.setGameMode(a.getString.toInt)
                case "enablePvp" => builder.setEnablePvp(a.getString.equals("true"))
                case "preventBlockBreak" => builder.setPreventBlockBreak(a.getString.equals("true"))
                case "difficulty" => builder.setDifficulty(a.getString.toInt)
                case "gametype" => builder.setGametype(a.getString)
                case "gamerules" => builder.setGamerules(JsonObject.readFrom(a.getString))
                case e =>
              }
              case e =>
            }
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
