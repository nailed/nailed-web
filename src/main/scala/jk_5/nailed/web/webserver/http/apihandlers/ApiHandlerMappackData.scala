package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{HttpResponseStatus, HttpRequest}
import jk_5.nailed.web.mappack.MappackRegistry
import jk_5.nailed.web.webserver.RoutedHandler

/**
 * No description given
 *
 * @author jk-5
 */
class ApiHandlerMappackData extends JsonHandler with RoutedHandler {
  override def handleGET(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder){
    val mappackId = this.getURLData.parameters.get("part1").get
    val mappack = MappackRegistry.getById(mappackId)
    if(mappack.isEmpty) rpd.error(HttpResponseStatus.NOT_FOUND, "Mappack not found")
    else rpd.ok(_.add("mappack", mappack.get.mappackDetailData))
  }
}
