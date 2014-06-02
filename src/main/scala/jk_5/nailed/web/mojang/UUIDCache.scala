package jk_5.nailed.web.mojang

import scala.collection.mutable
import org.apache.logging.log4j.{MarkerManager, LogManager}
import io.netty.handler.codec.http.HttpHeaders
import jk_5.jsonlibrary.{JsonObject, JsonArray}
import jk_5.nailed.web.auth.mojang.Yggdrasil
import jk_5.nailed.web.couchdb.CouchDB

/**
 * No description given
 *
 * @author jk-5
 */
object UuidCache {

  private val cache = mutable.ArrayBuffer[UuidCacheEntry]()

  final val profileUrl = "https://sessionserver.mojang.com/session/minecraft/profile/"
  val logger = LogManager.getLogger
  val marker = MarkerManager.getMarker("UuidCache")

  def loadCache(){
    val data = JsonObject.readFrom(CouchDB.getViewData("types", "uuidCache").get().getResponseBody).get("rows").asArray
    data.getValues.map(_.asObject.get("value").asObject).foreach(v => {
      val entry = new UuidCacheEntry(v.get("uuid").asString, v.get("name").asString)
      entry.readDB(v)
      cache += entry
    })
    logger.debug(s"Loaded uuid cache (Contains ${cache.size} entries)")
  }

  def fromUuid(uuid: String)(cb: (UuidCacheEntry) => Unit): Unit = cache.find(_.uuid == uuid) match {
    case Some(c) => cb(c)
    case None =>
      val geturl = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid
      val get = Yggdrasil.httpClient.prepareGet(geturl)
      logger.debug(marker, "Looking up username")
      logger.debug(marker, "> " + geturl)
      val future = Yggdrasil.httpClient.executeRequest(get.build())
      future.addListener(new Runnable {
        override def run(){
          val data = future.get().getResponseBody
          logger.debug(marker, "< " + data)
          val json = JsonObject.readFrom(data)
          val name = json.get("name").asString
          val entry = new UuidCacheEntry(uuid, name)
          cache += entry
          entry.populate(json.get("properties").asArray.find(_.asObject.get("name") == "textures").get.asObject.get("value").asString){entry => {
            cb(entry)
            entry.saveToDatabase()
          }}
        }
      }, Yggdrasil.executor)
  }

  def fromUsername(username: String)(cb: (UuidCacheEntry) => Unit) = cache.find(_.name == username) match {
    case Some(c) => cb(c)
    case None =>
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
          val entry = new UuidCacheEntry(uuid, username)
          cache += entry
          entry.populate(){entry => cb(entry)}
        }
      }, Yggdrasil.executor)
  }
}
