package jk_5.nailed.web.auth

import jk_5.jsonlibrary.JsonObject
import scala.collection.mutable
import java.lang.reflect.Field

/**
 * No description given
 *
 * @author jk-5
 */
private [auth] object PermissionContainerHelper {
  private [auth] val fields = mutable.HashMap[String, Field]()
  classOf[PermissionContainer].getDeclaredFields.foreach(f => {
    f.setAccessible(true)
    fields.put(f.getName, f)
  })
}

class PermissionContainer {

  var ircOperator = false
  var createMappack = false

  def read(json: JsonObject){
    json.getNames.foreach(n => PermissionContainerHelper.fields.get(n).foreach(_.set(this, json.get(n).asBoolean)))
  }
  def toJson: JsonObject = {
    val ret = new JsonObject
    PermissionContainerHelper.fields.foreach(f => ret.add(f._1, f._2.getBoolean(this)))
    ret
  }
}
