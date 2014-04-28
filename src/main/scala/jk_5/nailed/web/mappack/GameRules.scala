package jk_5.nailed.web.mappack

import scala.collection.mutable
import java.lang.reflect.Field
import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
private [mappack] object GameRules {
  private [mappack] val fields = mutable.HashMap[String, Field]()
  classOf[GameRules].getDeclaredFields.foreach(f => {
    f.setAccessible(true)
    fields.put(f.getName, f)
  })
}

class GameRules {
  var doFireTick = true
  var mobGriefing = true
  var keepInventory = false
  var doMobSpawning = true
  var doMobLoot = true
  var doTileDrops = true
  var commandBlockOutput = false
  var naturalRegeneration = true
  var doDaylightCycle = true

  def read(json: JsonObject){
    json.getNames.foreach(n => GameRules.fields.get(n).foreach(_.set(this, json.get(n).asBoolean)))
  }

  def toJson: JsonObject ={
    val ret = new JsonObject
    GameRules.fields.foreach(f => ret.add(f._1, f._2.getBoolean(this)))
    ret
  }

  def from(rules: GameRules){
    GameRules.fields.foreach(f => f._2.setBoolean(this, f._2.getBoolean(rules)))
  }
}
