package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http._
import io.netty.handler.codec.http.HttpMethod._
import io.netty.util.AttributeKey
import jk_5.nailed.web.webserver.http.handlers.AggregatedHandler

/**
 * No description given
 *
 * @author jk-5
 */
object JsonHandler {
  val rpd = AttributeKey.valueOf[Responder]("responder")
}

trait JsonHandler extends AggregatedHandler {

  override def handleAggregated(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    var rpd = ctx.channel().attr(JsonHandler.rpd).get()
    if(rpd == null){
      rpd = new Responder(ctx.channel())
      ctx.channel().attr(JsonHandler.rpd).set(rpd)
    }
    msg.getMethod match {
      case OPTIONS => this.handleOPTIONS(ctx, msg, rpd)
      case GET => this.handleGET(ctx, msg, rpd)
      case HEAD => this.handleHEAD(ctx, msg, rpd)
      case POST => this.handlePOST(ctx, msg, rpd)
      case PUT => this.handlePUT(ctx, msg, rpd)
      case PATCH => this.handlePATCH(ctx, msg, rpd)
      case DELETE => this.handleDELETE(ctx, msg, rpd)
      case TRACE => this.handleTRACE(ctx, msg, rpd)
      case CONNECT => this.handleCONNECT(ctx, msg, rpd)
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
