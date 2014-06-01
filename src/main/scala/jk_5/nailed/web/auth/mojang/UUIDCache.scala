package jk_5.nailed.web.auth.mojang

import io.netty.util.concurrent.GenericFutureListener
import jk_5.nailed.web.webserver.WebServer
import scala.collection.mutable
import org.apache.logging.log4j.{MarkerManager, LogManager}
import io.netty.handler.codec.http.HttpHeaders
import jk_5.jsonlibrary.JsonArray

/**
 * No description given
 *
 * @author jk-5
 */
object UUIDCache {

  private val usernamecache = mutable.HashMap[String, String]()
  private val uuidcache = mutable.HashMap[String, String]()
  final val profileUrl = "https://sessionserver.mojang.com/session/minecraft/profile/"
  val logger = LogManager.getLogger
  val marker = MarkerManager.getMarker("UUIDCache")

  def username(uuid: String, cb: (String) => Unit){
    if(usernamecache.contains(uuid)){
      cb(usernamecache.get(uuid).get)
    }else{
      val reqdata = new JsonArray().add(uuid).stringify
      val post = Yggdrasil.httpClient.preparePost("https://api.mojang.com/profiles/minecraft")
      post.addHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json")
      post.setBody(reqdata)
      logger.debug(marker, "Looking up username")
      logger.debug(marker, "> " + reqdata)
      val future = Yggdrasil.httpClient.executeRequest(post.build())
      future.addListener(new Runnable {
        override def run(){
          val data = future.get().getResponseBody
          logger.debug(marker, "< " + data)
          val json = JsonArray.readFrom(data).get(0).asObject
          val name = json.get("name").asString
          usernamecache.put(uuid, name)
          uuidcache.put(name, uuid)
          cb(name)
        }
      }, Yggdrasil.executor)
    }
  }

  def uuid(username: String, cb: (String) => Unit){
    if(uuidcache.contains(username)){
      cb(uuidcache.get(username).get)
    }else{
      val reqdata = new JsonArray().add(username).stringify
      val post = Yggdrasil.httpClient.preparePost("https://api.mojang.com/profiles/minecraft")
      post.addHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json")
      post.setBody(reqdata)
      logger.debug(marker, "Looking up username")
      logger.debug(marker, "> " + reqdata)
      val future = Yggdrasil.httpClient.executeRequest(post.build())
      future.addListener(new Runnable {
        override def run(){
          val data = future.get().getResponseBody
          logger.debug(marker, "< " + data)
          val json = JsonArray.readFrom(data).get(0).asObject
          val uuid = json.get("id").asString
          uuidcache.put(username, uuid)
          cb(uuid)
        }
      }, Yggdrasil.executor)
    }
  }
}
