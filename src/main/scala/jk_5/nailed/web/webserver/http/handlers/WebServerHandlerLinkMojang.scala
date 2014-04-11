package jk_5.nailed.web.webserver.http.handlers

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.RoutedHandler
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import jk_5.nailed.web.webserver.http.WebServerUtils
import jk_5.nailed.web.auth.mojang.Yggdrasil
import jk_5.nailed.web.auth.mojang.Yggdrasil.YggdrasilCallback

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerLinkMojang extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {
  override def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    if(msg.getMethod == HttpMethod.POST){
      val session = WebServerUtils.checkSession(ctx, msg)
      if(session.isDefined){
        val data = new HttpPostRequestDecoder(msg)
        val emailOpt = WebServerUtils.getPostEntry(data, "email")
        val passOpt = WebServerUtils.getPostEntry(data, "password")
        data.destroy()
        if(emailOpt.isEmpty || passOpt.isEmpty){
          WebServerUtils.sendError(ctx, "Invalid request: email or password undefined", HttpResponseStatus.BAD_REQUEST)
          return
        }
        val pass = passOpt.get
        val email = emailOpt.get
        Yggdrasil.lookupUid(email, pass, new YggdrasilCallback {
          override def onSuccess(uid: String){
            val user = session.get.getUser
            user.getAuthData.uid = uid
            user.getAuthData.verified = true
            user.saveToDatabase()
            WebServerUtils.sendOK(ctx)
          }
          override def onError(msg: String){
            WebServerUtils.sendError(ctx, msg)
          }
        })
      }
    }else WebServerUtils.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED)
  }
}
