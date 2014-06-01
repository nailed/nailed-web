package jk_5.nailed.web.auth.mojang

import org.apache.logging.log4j.{MarkerManager, LogManager}
import jk_5.jsonlibrary.JsonObject
import org.asynchttpclient.util.Base64
import io.netty.util.CharsetUtil

/**
 * No description given
 *
 * @author jk-5
 */
object PlayerSkinLookup {

  final val profileUrl = "https://sessionserver.mojang.com/session/minecraft/profile/"
  val logger = LogManager.getLogger
  val marker = MarkerManager.getMarker("SkinLookup")

  def getSkin(uid: String, callback: SkinCallback) = {
    val request = Yggdrasil.httpClient.prepareGet(profileUrl + uid).build()
    logger.debug(marker, "Looking up skin for UUID " + uid)
    logger.debug(marker, "> " + profileUrl + uid)
    val future = Yggdrasil.httpClient.executeRequest(request)
    future.addListener(new Runnable(){
      override def run(){
        val data = future.get().getResponseBody
        logger.debug(marker, "< " + data)
        if(data.isEmpty){
          callback.onError()
          return
        }
        logger.debug(marker, "Extracting result...")
        val json = JsonObject.readFrom(data)
        val url = JsonObject.readFrom(new String(Base64.decode(json.get("properties").asArray.get(0).asObject.get("value").asString), CharsetUtil.UTF_8)).get("textures").asObject.get("SKIN").asObject.get("url").asString
        callback.onSuccess(url)
      }
    }, Yggdrasil.executor)
  }

  trait SkinCallback {
    def onSuccess(url: String)
    def onError()
  }
}
