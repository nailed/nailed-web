package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{HttpResponseStatus, HttpRequest}
import jk_5.nailed.web.webserver.http.WebServerUtils
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import jk_5.nailed.web.auth.mojang.Yggdrasil
import jk_5.nailed.web.auth.mojang.Yggdrasil.YggdrasilCallback

/**
 * No description given
 *
 * @author jk-5
 */
class ApiHandlerLinkMojang extends JsonHandler {

  override def handlePOST(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder){
    val session = WebServerUtils.checkSession(ctx, msg)
    if(session.isDefined){
      val data = new HttpPostRequestDecoder(msg)
      val emailOpt = WebServerUtils.getPostEntry(data, "email")
      val passOpt = WebServerUtils.getPostEntry(data, "password")
      data.destroy()
      if(emailOpt.isEmpty || passOpt.isEmpty){
        rpd.error(HttpResponseStatus.BAD_REQUEST, "Invalid request: email or password undefined")
        return
      }
      val pass = passOpt.get
      val email = emailOpt.get
      Yggdrasil.lookupUid(email, pass, new YggdrasilCallback {
        override def onSuccess(uid: String){
          val user = session.get.getUser
          user.authData.uid = uid
          user.authData.verified = true
          user.saveToDatabase()
          rpd.ok
        }
        override def onError(msg: String) = rpd.error(msg)
      })
    }
  }
}
