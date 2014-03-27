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
class WebServerHandlerLogin extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {
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
      if(emailOpt.isEmpty || passOpt.isEmpty){
        val res = new JsonObject().add("status", "error").add("error", "Invalid request: email or password undefined")
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer(res.stringify, CharsetUtil.UTF_8)))
        data.destroy()
        return
      }
      val pass = passOpt.get
      val email = emailOpt.get
      val user = UserDatabase.getUser(email)
      if(user.isEmpty){
        val res = new JsonObject().add("status", "error").add("error", "Unknown email address")
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED, Unpooled.copiedBuffer(res.stringify, CharsetUtil.UTF_8)))
        data.destroy()
        return
      }
      val session = SessionManager.getSession(user.get, pass)
      if(session.isEmpty){
        val res = new JsonObject().add("status", "error").add("error", "Invalid password")
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED, Unpooled.copiedBuffer(res.stringify, CharsetUtil.UTF_8)))
        data.destroy()
        return
      }
      val res = new JsonObject().add("status", "ok").add("session", session.get.toJson).add("user", user.get.getUserInfo)
      ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(res.stringify, CharsetUtil.UTF_8)))
      data.destroy()
    }else if(msg.getMethod == HttpMethod.DELETE){
      val data = new QueryStringDecoder(msg.getUri)
      val params = data.parameters().get("session")
      if(params.size() != 1){
        val res = new JsonObject().add("status", "error").add("error", "Unknown Session")
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_ACCEPTABLE, Unpooled.copiedBuffer(res.stringify, CharsetUtil.UTF_8)))
        return
      }
      val removed = SessionManager.dropSession(params.get(0))
      val res = new JsonObject().add("status", "ok").add("removed", removed)
      ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(res.stringify, CharsetUtil.UTF_8)))
    }
  }
}
