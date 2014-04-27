package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{HttpResponseStatus, HttpRequest}
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import jk_5.nailed.web.webserver.http.WebServerUtils
import jk_5.nailed.web.auth.{SessionManager, UserDatabase}
import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
class ApiHandlerLogin extends JsonHandler {

  override def handlePOST(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder){
    val data = new HttpPostRequestDecoder(msg)
    val usernameOpt = WebServerUtils.getPostEntry(data, "username")
    val passOpt = WebServerUtils.getPostEntry(data, "password")
    data.destroy()
    if(usernameOpt.isEmpty || passOpt.isEmpty){
      WebServerUtils.sendError(ctx, "Invalid request: username or password undefined", HttpResponseStatus.BAD_REQUEST)
      return
    }
    val pass = passOpt.get
    val username = usernameOpt.get
    val user = UserDatabase.getUserByUsername(username)
    if(user.isEmpty){
      WebServerUtils.sendError(ctx, "Unknown username", HttpResponseStatus.UNAUTHORIZED)
      return
    }
    val session = SessionManager.getSession(user.get, pass)
    if(session.isEmpty){
      WebServerUtils.sendError(ctx, "Invalid password", HttpResponseStatus.UNAUTHORIZED)
      user.get.onFailedAuthAttempt(WebServerUtils.ipFor(ctx.channel(), msg), pass, "web")
      return
    }
    val r = WebServerUtils.okResponse(new JsonObject().add("session", session.get.toJson).add("user", user.get.getUserInfo))
    WebServerUtils.setSession(r, session.get)
    ctx.writeAndFlush(r)
  }

  override def handleDELETE(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder){
    val session = WebServerUtils.checkSession(ctx, msg)
    if(session.isDefined){
      val removed = SessionManager.dropSession(session.get)
      //TODO: move stuff like this to the responder
      val response = WebServerUtils.okResponse(new JsonObject().add("removed", removed))
      if(removed){
        WebServerUtils.removeSession(response, session.get)
      }
      ctx.writeAndFlush(response)
    }
  }
}
