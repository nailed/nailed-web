package jk_5.nailed.web.mojang

import jk_5.nailed.web.couchdb.{TCouchDBSerializable, DatabaseType}
import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.auth.mojang.Yggdrasil
import org.asynchttpclient.util.Base64
import io.netty.util.CharsetUtil

/**
 * No description given
 *
 * @author jk-5
 */
@DatabaseType("uuidCache")
case class UuidCacheEntry(var uuid: String, var name: String) extends TCouchDBSerializable{

  var skinBytesBase64: String = _

  override protected def writeToJsonForDB(data: JsonObject){
    data.add("uuid", this.uuid)
    data.add("name", this.name)
    data.add("skin", this.skinBytesBase64)
  }

  override protected def readFromJsonForDB(data: JsonObject){
    this.uuid = data.get("uuid").asString
    this.name = data.get("name").asString
    this.skinBytesBase64 = data.get("skin").asString
  }

  def populate(data: String = null)(cb: (UuidCacheEntry) => Unit){
    def decode(data: String, cb: (UuidCacheEntry) => Unit){
      //FIXME: The skin is null for people that don't have a custom skin
      val url = JsonObject.readFrom(new String(Base64.decode(data), CharsetUtil.UTF_8)).get("textures").asObject.get("SKIN").asObject.get("url").asString
      val future = Yggdrasil.httpClient.executeRequest(Yggdrasil.httpClient.prepareGet(url).build)
      future.addListener(new Runnable {
        override def run(){
          UuidCache.logger.debug(UuidCache.marker, "Downloaded skin " + url)
          skinBytesBase64 = Base64.encode(future.get().getResponseBodyAsBytes)
          cb(UuidCacheEntry.this)
          saveToDatabase()
        }
      }, Yggdrasil.executor)
    }
    if(data == null){
      val geturl = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid
      val get = Yggdrasil.httpClient.prepareGet(geturl)
      UuidCache.logger.debug(UuidCache.marker, "Looking up username")
      UuidCache.logger.debug(UuidCache.marker, "> " + geturl)
      val future = Yggdrasil.httpClient.executeRequest(get.build())
      future.addListener(new Runnable {
        override def run(){
          val data = future.get().getResponseBody
          UuidCache.logger.debug(UuidCache.marker, "< " + data)
          val json = JsonObject.readFrom(data)
          decode(json.get("properties").asArray.getValues.find(_.asObject.get("name").asString == "textures").get.asObject.get("value").asString, cb)
        }
      }, Yggdrasil.executor)
    }else{
      decode(data, cb)
    }
  }

  @inline def getSkinBytes: Array[Byte] = Base64.decode(this.skinBytesBase64)
}
