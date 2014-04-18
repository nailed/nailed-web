package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpRequest
import jk_5.jsonlibrary.JsonArray

/**
 * No description given
 *
 * @author jk-5
 */
class ApiHandlerMappackList extends JsonHandler {

  override def handleGET(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder){
    val list = new JsonArray //TODO: add stuff to the list
    rpd.ok(_.add("mappacks", list))
  }
}
