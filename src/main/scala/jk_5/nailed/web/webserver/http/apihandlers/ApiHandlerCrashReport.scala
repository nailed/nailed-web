package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{HttpResponseStatus, HttpRequest}
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import jk_5.nailed.web.webserver.http.WebServerUtils
import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.crash.CrashHandler

/**
 * No description given
 *
 * @author jk-5
 */
class ApiHandlerCrashReport extends JsonHandler {
  override def handlePOST(ctx: ChannelHandlerContext, msg: HttpRequest, rpd: Responder){
    val data = new HttpPostRequestDecoder(msg)
    val dataOpt = WebServerUtils.getPostEntry(data, "data")
    val stacktraceOpt = WebServerUtils.getPostEntry(data, "stacktrace")
    data.destroy()
    if(dataOpt.isEmpty || stacktraceOpt.isEmpty){
      rpd.error(HttpResponseStatus.BAD_REQUEST, "Invalid request: data or stacktrace undefined")
      return
    }
    val d = JsonObject.readFrom(dataOpt.get)
    CrashHandler.addCrashReport(stacktraceOpt.get, d)
    rpd.ok(j => {
      j.add("solution", null.asInstanceOf[String])
      j.add("url", "http://nailed.jk-5.tk/crash/1/")
    })
  }
}
