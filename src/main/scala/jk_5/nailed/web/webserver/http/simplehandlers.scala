package jk_5.nailed.web.webserver.http

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel._
import org.apache.logging.log4j.LogManager
import io.netty.handler.codec.http.{HttpRequest, FullHttpRequest, HttpResponseStatus}
import jk_5.nailed.web.webserver.irc.{IrcConnection, ProtocolIrc}
import jk_5.nailed.web.webserver.http.handlers.AggregatedHandler

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object HttpExceptionHandler extends ChannelHandlerAdapter {
  val logger = LogManager.getLogger
  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable){
    WebServerUtils.sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR).addListener(WebServerUtils.closeWhenRequested)
    this.logger.error("Caught error in pipeline", cause)
  }
}

@Sharable
object NotFoundHandler extends AggregatedHandler {
  override def handleAggregated(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    WebServerUtils.sendError(ctx, HttpResponseStatus.NOT_FOUND).addListener(WebServerUtils.closeWhenRequested)
  }
}

@Sharable
object HttpRequestLogger extends ChannelInboundHandlerAdapter {

  val channel = ProtocolIrc.getOrCreateChannel("#httplog")
  val connection = new IrcConnection("httplogger")
  ProtocolIrc.onConnect(connection)
  connection.join(channel)
  channel.setMode(connection, "+q")

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any){
    msg match {
      case e: HttpRequest =>
        ctx.fireChannelRead(e)
        connection.sendMessage(channel, s"${e.getMethod} ${e.getUri}")
      case e =>
        ctx.fireChannelRead(e)
    }
  }
}
