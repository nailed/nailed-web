package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{HttpResponseStatus, HttpRequest}
import jk_5.nailed.web.webserver.http.WebServerUtils
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import org.apache.logging.log4j.LogManager
import jk_5.nailed.web.mappack.MappackRegistry
import jk_5.nailed.web.game.ServerRegistry

/**
 * No description given
 *
 * @author jk-5
 */
class ApiHandlerLoadMappack extends JsonHandler {
  val logger = LogManager.getLogger
  override def handlePOST(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder){
    val data = new HttpPostRequestDecoder(msg)
    val idOpt = WebServerUtils.getPostEntry(data, "id")
    data.destroy()
    if(idOpt.isEmpty){
      rpd.error(HttpResponseStatus.BAD_REQUEST, "Invalid request: id undefined")
      return
    }
    val id = idOpt.get
    val mappack = MappackRegistry.getById(id)
    if(mappack.isEmpty){
      rpd.error(HttpResponseStatus.BAD_REQUEST, "Invalid request: unknown mappack")
      return
    }
    this.logger.info("Loading mappack {}", mappack.get.name)
    if(ServerRegistry.getServers.size > 0){
      val server = ServerRegistry.getServers(0)
      mappack.get.load(server)
    }
    rpd.ok
  }
}
