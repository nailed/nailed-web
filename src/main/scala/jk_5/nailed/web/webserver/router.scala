package jk_5.nailed.web.webserver

import io.netty.channel.ChannelHandler.Sharable
import io.netty.handler.codec.http.{HttpResponseStatus, FullHttpRequest}
import io.netty.channel.{ChannelHandlerAdapter, ChannelHandler, ChannelHandlerContext}
import jk_5.nailed.web.webserver.http.{MultiplexingUrlResolver, URLData, WebServerUtils, NotFoundHandler}
import io.netty.util.ReferenceCounted

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
class RouterHandler(private val resolver: MultiplexingUrlResolver, private val handlerName: String) extends ChannelHandlerAdapter {

  override def channelRead(ctx: ChannelHandlerContext, msg: Any){
    msg match {
      case m: FullHttpRequest =>
        val url = m.getUri
        val urlData = this.resolver.getValueForURL(url)
        if(urlData.isEmpty) ctx.pipeline().replace(this.handlerName, this.handlerName, NotFoundHandler)
        else{
          val handler = urlData.get.getHandler.newInstance()
          handler match{
            case h: RoutedHandler =>
              h.setURLData(urlData.get)
              h.setRouterHandler(this)
            case h =>
          }
          ctx.pipeline().replace(this.handlerName, this.handlerName, handler)
        }
      case m =>
    }
    msg match {
      case c: ReferenceCounted => c.retain()
      case c =>
    }
    ctx.fireChannelRead(msg)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable){
    if(ctx.channel().isActive) WebServerUtils.sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR)
    cause.printStackTrace()
    //NexusLog.log(Level.SEVERE, cause, "Error in netty pipeline at RouterHandler.exceptionCaught")
  }
}

trait RoutedHandler extends ChannelHandler {
  private var _urlData: URLData = _
  private var _routerHandler: RouterHandler = _

  def setRouterHandler(handler: RouterHandler) = this._routerHandler = handler
  def getRouterHandler = this._routerHandler
  def setURLData(urlData: URLData) = this._urlData = urlData
  def getURLData = this._urlData
}