package jk_5.nailed.web.webserver.http

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel._
import org.apache.logging.log4j.LogManager
import io.netty.handler.codec.http.{HttpRequest, FullHttpRequest, HttpResponseStatus}
import jk_5.nailed.web.webserver.irc.{IrcConnection, ProtocolIrc}

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object HttpExceptionHandler extends ChannelHandlerAdapter {
  val logger = LogManager.getLogger
  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable){
    WebServerUtils.sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR).addListener(ChannelFutureListener.CLOSE)
    this.logger.error("Caught error in pipeline", cause)
  }
}

//TODO: fix this
@Sharable
object NotFoundHandler extends SimpleChannelInboundHandler[FullHttpRequest] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    val future = WebServerUtils.sendError(ctx, HttpResponseStatus.NOT_FOUND)
    WebServerUtils.closeIfRequested(msg, future)
  }
}

@Sharable
object HttpRequestLogger extends ChannelInboundHandlerAdapter {

  val channel = ProtocolIrc.getOrCreateChannel("#httplog")
  val connection = new IrcConnection("httplogger")
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
