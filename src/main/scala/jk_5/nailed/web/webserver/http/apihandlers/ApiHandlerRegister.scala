package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{HttpResponseStatus, HttpRequest}
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import jk_5.nailed.web.webserver.http.WebServerUtils
import jk_5.nailed.web.auth.{SessionManager, UserDatabase}
import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.mail.{Mailer, MailTemplates}

/**
 * No description given
 *
 * @author jk-5
 */
class ApiHandlerRegister extends JsonHandler {

  override def handlePOST(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder){
    val data = new HttpPostRequestDecoder(msg)
    val usernameOpt = WebServerUtils.getPostEntry(data, "username")
    val emailOpt = WebServerUtils.getPostEntry(data, "email")
    val passOpt = WebServerUtils.getPostEntry(data, "password")
    val nameOpt = WebServerUtils.getPostEntry(data, "name")
    data.destroy()
    if(usernameOpt.isEmpty || emailOpt.isEmpty || passOpt.isEmpty || nameOpt.isEmpty){
      rpd.error(HttpResponseStatus.BAD_REQUEST, "Invalid request: username, email, password or name undefined")
      return
    }
    val username = usernameOpt.get
    val pass = passOpt.get
    val email = emailOpt.get
    val name = nameOpt.get
    if(UserDatabase.getUserByUsername(username).isDefined){
      rpd.error(HttpResponseStatus.BAD_REQUEST, "An user with that username already exists")
      return
    }
    if(UserDatabase.getUserByEmail(email).isDefined){
      rpd.error(HttpResponseStatus.BAD_REQUEST, "An user with that email already exists")
      return
    }
    val user = UserDatabase.createUser(username, email, pass, name)
    val session = SessionManager.getSession(user, pass)
    //TODO: move to responder
    val r = WebServerUtils.okResponse(new JsonObject().add("user", user.getUserInfo).add("session", session.get.toJson))
    WebServerUtils.setSession(r, session.get)
    ctx.writeAndFlush(r)

    val template = MailTemplates.parseTemplate("accountCreated.html")
    Mailer.sendMail(user, "Account created", template)
  }
}
