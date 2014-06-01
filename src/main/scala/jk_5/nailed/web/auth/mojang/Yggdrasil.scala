package jk_5.nailed.web.auth.mojang

import io.netty.handler.codec.http.HttpHeaders
import jk_5.jsonlibrary.JsonObject
import java.util.concurrent.Executors
import org.apache.logging.log4j.{MarkerManager, LogManager}
import jk_5.nailed.web.LogUtils
import io.netty.util.CharsetUtil
import org.asynchttpclient.AsyncHttpClient

/**
 * No description given
 *
 * @author jk-5
 */
object Yggdrasil {
  val httpClient = new AsyncHttpClient
  val executor = Executors.newCachedThreadPool()
  val logger = LogManager.getLogger
  val marker = MarkerManager.getMarker("Yggdrasil")

  private def getJsonTemplate = new JsonObject().add("agent", new JsonObject().add("name", "Minecraft").add("version", 1))
  def lookupUid(username: String, password: String, callback: YggdrasilCallback){
    val cb = Option(callback)
    val builder = this.httpClient.preparePost("https://authserver.mojang.com/authenticate")
    val body = this.getJsonTemplate.set("username", username).set("password", password).stringify
    builder.setBody(body.getBytes(CharsetUtil.UTF_8))
    builder.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=utf-8")
    builder.setHeader(HttpHeaders.Names.CONTENT_LENGTH, body.length.toString)
    val req = builder.build()
    val mask = LogUtils.mask(password)
    this.logger.debug(this.marker, "Lookup for {}:{}", username, mask)
    this.logger.debug(this.marker, "> {}", body.replace(password, mask))
    val future = this.httpClient.executeRequest(req)
    future.addListener(new Runnable {
      override def run(){
        val res = future.get()
        logger.debug(marker, "< {}", res.getResponseBody)
        val data = JsonObject.readFrom(res.getResponseBody)
        if(data.get("error") != null){
          val errorMessage = data.get("errorMessage").asString
          cb.foreach(_.onError(errorMessage))
        }else{
          val uid = data.get("selectedProfile").asObject.get("id").asString
          cb.foreach(_.onSuccess(uid))
        }
      }
    }, this.executor)
  }

  trait YggdrasilCallback {
    def onSuccess(uid: String)
    def onError(msg: String)
  }
}
