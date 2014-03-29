package jk_5.nailed.web.webserver.http.handlers

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import jk_5.nailed.web.webserver.RoutedHandler
import jk_5.nailed.web.auth.{UserDatabase, SessionManager}
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.webserver.http.WebServerUtils

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerLogin extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {
  override def messageReceived(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    if(msg.getMethod == HttpMethod.POST){
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
      val user = UserDatabase.getUser(email)
      if(user.isEmpty){
        val res = new JsonObject().add("status", "error").add("error", "Unknown email address")
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED, Unpooled.copiedBuffer(res.stringify, CharsetUtil.UTF_8)))
        return
      }
      val session = SessionManager.getSession(user.get, pass)
      if(session.isEmpty){
        val res = new JsonObject().add("status", "error").add("error", "Invalid password")
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED, Unpooled.copiedBuffer(res.stringify, CharsetUtil.UTF_8)))
        return
      }
      val res = new JsonObject().add("status", "ok").add("session", session.get.toJson).add("user", user.get.getUserInfo)
      val r = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(res.stringify, CharsetUtil.UTF_8))
      WebServerUtils.setCookie(r, "uid", user.get.getID.toString)
      WebServerUtils.setCookie(r, "sessid", session.get.getID.toString)
      ctx.writeAndFlush(r)
    }else if(msg.getMethod == HttpMethod.DELETE){
      val session = WebServerUtils.checkSession(ctx, msg)
      if(session.isDefined){
        val removed = SessionManager.dropSession(session.get)
        val response = WebServerUtils.jsonResponse(new JsonObject().add("status", "ok").add("removed", removed))
        if(removed){
          WebServerUtils.removeCookie(response, "uid")
          WebServerUtils.removeCookie(response, "sessid")
        }
        ctx.writeAndFlush(response)
      }
    }else WebServerUtils.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED)
  }
}
