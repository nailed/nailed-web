package jk_5.nailed.web.webserver.http.handlers

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.RoutedHandler
import jk_5.nailed.web.auth.{UserDatabase, SessionManager}
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.webserver.http.WebServerUtils

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerLogin extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {
  override def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    if(msg.getMethod == HttpMethod.POST){
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
      val user = UserDatabase.getUser(email)
      if(user.isEmpty){
        WebServerUtils.sendError(ctx, "Unknown email addres", HttpResponseStatus.UNAUTHORIZED)
        return
      }
      val session = SessionManager.getSession(user.get, pass)
      if(session.isEmpty){
        WebServerUtils.sendError(ctx, "Invalid password", HttpResponseStatus.UNAUTHORIZED)
        return
      }
      val r = WebServerUtils.okResponse(new JsonObject().add("session", session.get.toJson).add("user", user.get.getUserInfo))
      WebServerUtils.setSession(r, session.get)
      ctx.writeAndFlush(r)
    }else if(msg.getMethod == HttpMethod.DELETE){
      val session = WebServerUtils.checkSession(ctx, msg)
      if(session.isDefined){
        val removed = SessionManager.dropSession(session.get)
        val response = WebServerUtils.okResponse(new JsonObject().add("removed", removed))
        if(removed){
          WebServerUtils.removeSession(response, session.get)
        }
        ctx.writeAndFlush(response)
      }
    }else WebServerUtils.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED)
  }
}
