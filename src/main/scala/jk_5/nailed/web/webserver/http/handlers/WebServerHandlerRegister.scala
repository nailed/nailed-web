package jk_5.nailed.web.webserver.http.handlers

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.RoutedHandler
import jk_5.nailed.web.auth.{UserDatabase, SessionManager}
import io.netty.handler.codec.http.multipart.{Attribute, HttpPostRequestDecoder}
import scala.Some
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import jk_5.jsonlibrary.JsonObject

/**
  * No description given
  *
  * @author jk-5
  */
class WebServerHandlerRegister extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {
  override def messageReceived(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    if(msg.getMethod == HttpMethod.POST){
      val data = new HttpPostRequestDecoder(msg)
      var emailOpt: Option[String] = None
      if(data.getBodyHttpData("email") != null){
        emailOpt = Some(data.getBodyHttpData("email").asInstanceOf[Attribute].getValue)
      }
      var passOpt: Option[String] = None
      if(data.getBodyHttpData("password") != null){
        passOpt = Some(data.getBodyHttpData("password").asInstanceOf[Attribute].getValue)
      }
      var nameOpt: Option[String] = None
      if(data.getBodyHttpData("name") != null){
        nameOpt = Some(data.getBodyHttpData("name").asInstanceOf[Attribute].getValue)
      }
      if(emailOpt.isEmpty || passOpt.isEmpty || nameOpt.isEmpty){
        val res = new JsonObject().add("status", "error").add("error", "Invalid request: email, password or name undefined")
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer(res.stringify, CharsetUtil.UTF_8)))
        data.destroy()
        return
      }
      val pass = passOpt.get
      val email = emailOpt.get
      val name = nameOpt.get
      if(UserDatabase.getUser(email).isDefined){
        val res = new JsonObject().add("status", "error").add("error", "An user with that email address already exists")
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer(res.stringify, CharsetUtil.UTF_8)))
        data.destroy()
        return
      }
      val user = UserDatabase.createUser(email, pass, name)
      val session = SessionManager.getSession(user, pass)
      val res = new JsonObject().add("status", "ok").add("user", user.getUserInfo).add("session", session.get.toJson)
      ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(res.stringify, CharsetUtil.UTF_8)))
      data.destroy()
    }
  }
}
