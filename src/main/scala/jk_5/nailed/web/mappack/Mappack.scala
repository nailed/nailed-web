package jk_5.nailed.web.mappack

import jk_5.jsonlibrary.{JsonArray, JsonObject}
import jk_5.nailed.web.couchdb.{DatabaseType, TCouchDBSerializable}
import jk_5.nailed.web.game.GameServer
import jk_5.nailed.web.webserver.ipc.packet.PacketLoadMappackMeta

/**
 * No description given
 *
 * @author jk-5
 */
@DatabaseType("mappack")
class Mappack(private var mappackId: String, var name: String) extends TCouchDBSerializable {
  def this(json: JsonObject){
    this(json.get("mpid").asString, json.get("name").asString)
    this.readDB(json)
  }

  var timesPlayed = 0
  val filestore = new MappackFilestore(this)
  var spawnpoint = new Location(0, 64, 0)
  var defaultGamemode = 0
  var difficulty = 0
  val spawnRules = new SpawnRules
  val gameRules = new GameRules
  var gametype = "default"

  def mpid = this.mappackId

  def mappackListData: JsonObject = new JsonObject()
    .add("mpid", this.mappackId)
    .add("name", this.name)
    .add("timesPlayedUser", -1) //TODO
    .add("timesPlayedGlobal", this.timesPlayed)
    .add("tags", new JsonArray().add("PvP").add("PvE"))

  def mappackDetailData: JsonObject = new JsonObject()
    .add("mpid", this.mappackId)
    .add("name", this.name)
    .add("timesPlayedUser", -1) //TODO
    .add("timesPlayedGlobal", this.timesPlayed)
    .add("tags", new JsonArray().add("PvP").add("PvE"))

  def writeToJsonForDB(data: JsonObject){
    data.add("mpid", this.mappackId)
    data.add("name", this.name)
    data.add("worldFiles", this.filestore.toJson)
    data.add("plays", this.timesPlayed)
    data.add("spawnpoint", this.spawnpoint.toJson)
    data.add("defaultGamemode", this.defaultGamemode)
    data.add("difficulty", this.difficulty)
    data.add("spawns", this.spawnRules.toJson)
    data.add("gamerules", this.gameRules.toJson)
    data.add("gametype", this.gametype)
  }

  def readFromJsonForDB(data: JsonObject){
    this.mappackId = data.get("mpid").asString
    this.name = data.get("name").asString
    this.filestore.readData(data.get("worldFiles").asArray)
    this.timesPlayed = data.get("plays").asInt
    this.spawnpoint = Location.fromJson(data.get("spawnpoint").asObject)
    this.defaultGamemode = data.get("defaultGamemode").asInt
    this.difficulty = data.get("difficulty").asInt
    this.spawnRules.read(data.get("spawns").asObject)
    this.gameRules.read(data.get("gamerules").asObject)
    if(data.get("gametype") != null) this.gametype = data.get("gametype").asString
  }

  def load(server: GameServer){
    val packet = new PacketLoadMappackMeta
    val json = new JsonObject
    this.writeToJsonForDB(json)
    packet.data = json
    packet.id = this.mpid
    server.sendPacket(packet)
  }
}
