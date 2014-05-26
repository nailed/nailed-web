package jk_5.nailed.web.webserver.http.response

import io.netty.handler.codec.http.HttpResponseStatus
import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
case class ErrorResponse(response: HttpResponseStatus){
  val json = new JsonObject().add("status", "error").add("error", new JsonObject().add("type", "http").add("code", response.code()).add("reason", response.reasonPhrase()))
}
