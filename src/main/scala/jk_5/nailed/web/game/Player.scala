package jk_5.nailed.web.game

import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
case class Player(id: String, name: String){

  def toJson = new JsonObject().add("id", this.id).add("name", this.name)
}
