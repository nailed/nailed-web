package jk_5.nailed.web.mappack

import scala.collection.mutable
import java.lang.reflect.Field
import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
private [mappack] object SpawnRules {
  private [mappack] val fields = mutable.HashMap[String, Field]()
    classOf[SpawnRules].getDeclaredFields.foreach(f => {
    f.setAccessible(true)
    fields.put(f.getName, f)
  })
}

class SpawnRules {
  var zombie = true
  var creeper = true
  var skeleton = true
  var slime = true
  var witch = true
  var spider = true
  var cavespider = true
  var wolf = true
  var pigzombie = true
  var blaze = true
  var ghast = true
  var magmaCube = true
  var silverfish = true
  var witherSkeleton = true

  var bat = true
  var cow = true
  var sheep = true
  var pig = true
  var chicken = true
  var horse = true
  var ocelot = true
  var mooshroom = true
  var squid = true
  var villager = true

  def read(json: JsonObject){
    json.getNames.foreach(n => SpawnRules.fields.get(n).foreach(_.set(this, json.get(n).asBoolean)))
  }

  def toJson: JsonObject ={
    val ret = new JsonObject
    SpawnRules.fields.foreach(f => ret.add(f._1, f._2.getBoolean(this)))
    ret
  }

  def from(rules: SpawnRules){
    SpawnRules.fields.foreach(f => f._2.setBoolean(this, f._2.getBoolean(rules)))
  }
}
