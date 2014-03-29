package jk_5.nailed.web.auth.mojang

import com.ning.http.client.AsyncHttpClient
import io.netty.handler.codec.http.HttpHeaders
import jk_5.jsonlibrary.JsonObject
import java.util.concurrent.Executors

/**
 * No description given
 *
 * @author jk-5
 */
object Yggdrasil {
  private val httpClient = new AsyncHttpClient
  private val executor = Executors.newCachedThreadPool()

  private def getJsonTemplate = new JsonObject().add("agent", new JsonObject().add("name", "Minecraft").add("version", 1))
  def lookupUid(username: String, password: String, callback: YggdrasilCallback){
    val cb = Option(callback)
    val builder = this.httpClient.preparePost("https://authserver.mojang.com/authenticate")
    builder.setHeader(HttpHeaders.Names.CONTENT_TYPE.toString, "application/json")
    builder.setBody(this.getJsonTemplate.set("username", username).set("password", password).stringify)
    val req = builder.build()
    val future = this.httpClient.executeRequest(req)
    future.addListener(new Runnable {
      override def run(){
        val res = future.get()
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
