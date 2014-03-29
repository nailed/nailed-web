package jk_5.nailed.web.webserver.http.handlers

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.RoutedHandler
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import jk_5.jsonlibrary.JsonObject
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import jk_5.nailed.web.webserver.http.WebServerUtils
import jk_5.nailed.web.auth.mojang.Yggdrasil
import jk_5.nailed.web.auth.mojang.Yggdrasil.YggdrasilCallback

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerLinkMojang extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {
  override def messageReceived(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    if(msg.getMethod == HttpMethod.POST){
      val session = WebServerUtils.checkSession(ctx, msg)
      if(session.isDefined){
        val data = new HttpPostRequestDecoder(msg)
        val emailOpt = WebServerUtils.getPostEntry(data, "email")
        val passOpt = WebServerUtils.getPostEntry(data, "password")
        data.destroy()
        if(emailOpt.isEmpty || passOpt.isEmpty){
          val res = new JsonObject().add("status", "error").add("error", "Invalid request: email or password undefined")
          ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer(res.stringify, CharsetUtil.UTF_8)))
          return
        }
        val pass = passOpt.get
        val email = emailOpt.get
        Yggdrasil.lookupUid(email, pass, new YggdrasilCallback {
          override def onSuccess(uid: String){
            val user = session.get.getUser.get
            user.getAuthData.uid = uid
            user.getAuthData.verified = true
            user.saveToDatabase()
            WebServerUtils.sendJson(ctx, new JsonObject().add("status", "ok"))
          }
          override def onError(msg: String){
            WebServerUtils.sendJson(ctx, new JsonObject().add("status", "error").add("error", msg))
          }
        })
      }
    }else WebServerUtils.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED)
  }
}
