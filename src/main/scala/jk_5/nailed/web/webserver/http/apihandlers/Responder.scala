package jk_5.nailed.web.webserver.http.apihandlers

import io.netty.handler.codec.http.{HttpVersion, HttpResponseStatus, DefaultFullHttpResponse}
import jk_5.jsonlibrary.JsonObject
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import io.netty.channel.Channel

/**
 * No description given
 *
 * @author jk-5
 */
class Responder(private val channel: Channel){
  def json(json: JsonObject, status: HttpResponseStatus = HttpResponseStatus.OK) = channel.writeAndFlush(this.fromJson(json, status))
  def ok(configure: (JsonObject) => Unit){
    val obj = new JsonObject().add("status", "ok")
    configure(obj)
    this.json(obj)
  }
  def ok = this.json(new JsonObject().add("status", "ok"))
  def error(msg: String) = this.json(new JsonObject().add("status", "error").add("error", msg))
  def error(error: HttpResponseStatus, msg: String) = this.json(new JsonObject().add("status", "error").add("error", msg), error)
  def error(error: HttpResponseStatus){
    if(error == HttpResponseStatus.OK) throw new IllegalArgumentException("OK is not a valid error")
    this.error(error, error.toString)
  }

  //Some error codes
  def wrongMethod() = this.error(HttpResponseStatus.METHOD_NOT_ALLOWED)
  def notFound() = this.error(HttpResponseStatus.NOT_FOUND)

  private def fromJson(json: JsonObject, status: HttpResponseStatus = HttpResponseStatus.OK): DefaultFullHttpResponse = {
    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(json.stringify, CharsetUtil.UTF_8))
  }
}
