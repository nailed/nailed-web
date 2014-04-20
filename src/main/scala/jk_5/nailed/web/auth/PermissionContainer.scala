package jk_5.nailed.web.auth

import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
class PermissionContainer {

  var ircOperator = false

  def read(json: JsonObject){
    this.ircOperator = json.get("ircOperator").asBoolean
  }
  def toJson = new JsonObject().set("ircOperator", this.ircOperator)
}
