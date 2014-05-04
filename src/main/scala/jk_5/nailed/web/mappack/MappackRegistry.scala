package jk_5.nailed.web.mappack

import org.apache.logging.log4j.LogManager
import scala.collection.mutable
import jk_5.nailed.web.couchdb.CouchDB
import jk_5.nailed.web.NailedWeb
import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
object MappackRegistry {

  private val logger = LogManager.getLogger
  val mappacks = mutable.ArrayBuffer[Mappack]()

  private val cdbFuture = CouchDB.getViewData("types", "mappacks")
  cdbFuture.addListener(new Runnable {
    override def run(){
      val json = JsonObject.readFrom(cdbFuture.get().getResponseBody).get("rows").asArray
      json.getValues.foreach(v => mappacks += new Mappack(v.asObject.get("value").asObject))
      logger.debug("Loaded all mappacks from database")
    }
  }, NailedWeb.worker)

  def getById(id: String): Option[Mappack] = this.mappacks.find(_.mpid == id)

  def addMappack(mappack: Mappack){
    this.mappacks += mappack
  }
}
