package jk_5.nailed.web.auth.mojang

import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
class AuthData {

  var verified = false
  var uid: String = null

  def read(json: JsonObject){
    if(json.get("verified") != null) this.verified = json.get("verified").asBoolean
    if(json.get("uid") != null && this.verified) this.uid = json.get("uid").asString
  }
  def toJson = new JsonObject().set("verified", this.verified).set("uid", this.uid)
}
