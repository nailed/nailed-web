package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.{MimeTypesLookup, RoutedHandler}
import jk_5.nailed.web.auth.UserDatabase
import jk_5.nailed.web.couchdb.UID
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import jk_5.nailed.web.webserver.http.WebServerUtils

/**
 * No description given
 *
 * @author jk-5
 */
object ApiHandlerActivateAccount {
  val res = Unpooled.copiedBuffer("<html><head><title>Nailed - Account activated</title></head><body><h2>Your account was successfully activated!</h2></body></html>", CharsetUtil.UTF_8)
}

class ApiHandlerActivateAccount extends JsonHandler with RoutedHandler {

  override def handleGET(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder){
    val decoder = new QueryStringDecoder(msg.getUri)
    val pretty = decoder.parameters().containsKey("pretty")
    val userid = this.getURLData.parameters.get("part1")
    if(userid.isEmpty){
      rpd internalServerError()
      return
    }
    val user = UserDatabase.getUser(UID(userid.get))
    if(user.isEmpty){
      rpd.badRequest("Invalid userid")
      return
    }
    if(user.get.activated){
      rpd.error("User was already activated")
      return
    }
    if(pretty){
      val r = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(ApiHandlerActivateAccount.res))
      r.headers().set(HttpHeaders.Names.CONTENT_TYPE, MimeTypesLookup.getMimeTypeFromExt("html"))
      val future = ctx.writeAndFlush(r)
      WebServerUtils.closeIfRequested(msg, future)
    }else{
      rpd.ok
    }
    user.get.activated = true
    user.get.saveToDatabase()
  }
}
