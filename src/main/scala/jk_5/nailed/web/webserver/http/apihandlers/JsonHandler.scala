package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import io.netty.handler.codec.http.HttpMethod._
import io.netty.util.AttributeKey

/**
 * No description given
 *
 * @author jk-5
 */
object JsonHandler {
  val rpd = AttributeKey.valueOf[Responder]("responder")
}

trait JsonHandler extends SimpleChannelInboundHandler[HttpObject] {

  private var request: HttpRequest = null

  override def channelRead0(ctx: ChannelHandlerContext, msg: HttpObject){
    msg match {
      case req: HttpRequest => this.request = req
      case _: LastHttpContent =>
        var rpd = ctx.channel().attr(JsonHandler.rpd).get()
        if(rpd == null){
          rpd = new Responder(ctx.channel())
          ctx.channel().attr(JsonHandler.rpd).set(rpd)
        }
        this.request.getMethod match {
          case OPTIONS => this.handleOPTIONS(ctx, this.request, rpd)
          case GET => this.handleGET(ctx, this.request, rpd)
          case HEAD => this.handleHEAD(ctx, this.request, rpd)
          case POST => this.handlePOST(ctx, this.request, rpd)
          case PUT => this.handlePUT(ctx, this.request, rpd)
          case PATCH => this.handlePATCH(ctx, this.request, rpd)
          case DELETE => this.handleDELETE(ctx, this.request, rpd)
          case TRACE => this.handleTRACE(ctx, this.request, rpd)
          case CONNECT => this.handleCONNECT(ctx, this.request, rpd)
        }
        this.request = null
      case _: HttpContent =>
    }
  }

  //Add default implementations for each method and throw MethodNotAllowed when it is not overridden
  def handleOPTIONS(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder) = rpd.wrongMethod()
  def handleGET(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder) = rpd.wrongMethod()
  def handleHEAD(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder) = rpd.wrongMethod()
  def handlePOST(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder) = rpd.wrongMethod()
  def handlePUT(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder) = rpd.wrongMethod()
  def handlePATCH(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder) = rpd.wrongMethod()
  def handleDELETE(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder) = rpd.wrongMethod()
  def handleTRACE(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder) = rpd.wrongMethod()
  def handleCONNECT(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder) = rpd.wrongMethod()
}
