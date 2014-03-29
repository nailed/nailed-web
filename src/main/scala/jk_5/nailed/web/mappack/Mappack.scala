package jk_5.nailed.web.mappack

import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
class Mappack(private val mappackId: String) {

  var name = this.mappackId

  def toJson: JsonObject = new JsonObject().add("id", this.mappackId).add("name", this.name)
  def id = this.mappackId
}
