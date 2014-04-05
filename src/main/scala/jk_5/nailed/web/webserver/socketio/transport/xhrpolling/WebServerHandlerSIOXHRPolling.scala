package jk_5.nailed.web.webserver.socketio.transport.xhrpolling

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import jk_5.nailed.web.webserver.RoutedHandler
import io.netty.handler.codec.http._
import java.util.UUID
import jk_5.nailed.web.webserver.socketio.handler.WebServerHandlerSIOHandshake
import jk_5.nailed.web.webserver.socketio.ClientRegistry
import jk_5.nailed.web.webserver.socketio.packet.NoopSIOPacket
import jk_5.nailed.web.webserver.http.WebServerUtils

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerSIOXHRPolling extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {
  override def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    val queryDecoder = new QueryStringDecoder(msg.getUri)
    val uid = UUID.fromString(this.getURLData.getParameters.get("part2").get)
    this.logger.debug(marker, "Incoming XHR connection from client {}", uid)
    val future = WebServerHandlerSIOHandshake.futures.get(uid)
    val session = WebServerHandlerSIOHandshake.sessions.get(uid)
    val origin = msg.headers().get(HttpHeaders.Names.ORIGIN)
    if(queryDecoder.parameters().containsKey("disconnect")){
      //val client = ctx.channel().attr(ClientRegistry.sioClient).get()
      //client.disconnect()
      ctx.channel().writeAndFlush(new NoopSIOPacket)
    }else if(msg.getMethod == HttpMethod.POST){

    }else if(msg.getMethod == HttpMethod.GET){
      var client = ctx.channel().attr(ClientRegistry.sioClient).get()
      if(client == null){
        client = new XHRPollingClient(ctx.channel(), uid, session.get)
        //TODO
      }
    }else{
      val future = WebServerUtils.sendError(ctx, HttpResponseStatus.BAD_REQUEST)
      WebServerUtils.closeIfRequested(msg, future)
    }
  }
}
