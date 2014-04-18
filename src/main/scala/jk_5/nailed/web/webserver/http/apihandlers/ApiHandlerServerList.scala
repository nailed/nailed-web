package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpRequest
import jk_5.jsonlibrary.JsonArray
import jk_5.nailed.web.game.ServerRegistry

/**
 * No description given
 *
 * @author jk-5
 */
class ApiHandlerServerList extends JsonHandler {

  override def handleGET(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder){
    val list = new JsonArray
    ServerRegistry.getServers.foreach(s => list.add(s.toJson))
    rpd.ok(_.add("servers", list))
  }
}
