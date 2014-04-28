package jk_5.nailed.web.mappack

import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
object Location {
  def fromJson(data: JsonObject) = new Location(data.get("x").asDouble, data.get("y").asDouble, data.get("z").asDouble, data.get("yaw").asFloat, data.get("pitch").asFloat)
}

case class Location(x: Double, y: Double, z: Double, yaw: Float = 0, pitch: Float = 0) {
  def toJson = new JsonObject().add("x", x).add("y", y).add("z", z).add("yaw", yaw).add("pitch", pitch)
}
