package jk_5.nailed.web.mappack

import jk_5.jsonlibrary.{JsonArray, JsonObject}

/**
 * No description given
 *
 * @author jk-5
 */
class Mappack(private val mappackId: String, var name: String){

  var timesPlayed = 0

  def toJson: JsonObject = new JsonObject()
    .add("id", this.mappackId)
    .add("name", this.name)
    .add("timesPlayedUser", -1) //TODO
    .add("timesPlayedGlobal", this.timesPlayed)
    .add("tags", new JsonArray().add("PvP").add("PvE"))
  def id = this.mappackId
}
