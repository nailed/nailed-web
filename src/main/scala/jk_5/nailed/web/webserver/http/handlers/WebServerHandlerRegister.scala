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
class WebServerHandlerRegister extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {
  override def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    if(msg.getMethod == HttpMethod.POST){
      val data = new HttpPostRequestDecoder(msg)
      val emailOpt = WebServerUtils.getPostEntry(data, "email")
      val passOpt = WebServerUtils.getPostEntry(data, "password")
      val nameOpt = WebServerUtils.getPostEntry(data, "name")
      data.destroy()
      if(emailOpt.isEmpty || passOpt.isEmpty || nameOpt.isEmpty){
        WebServerUtils.sendError(ctx, "Invalid request: email, password or name undefined", HttpResponseStatus.BAD_REQUEST)
        return
      }
      val pass = passOpt.get
      val email = emailOpt.get
      val name = nameOpt.get
      if(UserDatabase.getUser(email).isDefined){
        WebServerUtils.sendError(ctx, "An user with that email address already exists", HttpResponseStatus.BAD_REQUEST)
        return
      }
      val user = UserDatabase.createUser(email, pass, name)
      val session = SessionManager.getSession(user, pass)
      val r = WebServerUtils.okResponse(new JsonObject().add("user", user.getUserInfo).add("session", session.get.toJson))
      WebServerUtils.setSession(r, session.get)
      ctx.writeAndFlush(r)
    }else WebServerUtils.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED)
  }
}
