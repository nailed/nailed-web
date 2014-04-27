package jk_5.nailed.web.mappack

import io.netty.handler.codec.http.multipart.FileUpload
import jk_5.jsonlibrary.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
class MappackBuilder {

  private var id: String = _
  private var name: String = _
  private var worldType: String = _
  private var spawnAnimals: Boolean = _
  private var spawnMonsters: Boolean = _
  private var mapFile: FileUpload = _
  private var worldSource: String = _
  private var gameMode: Int = _
  private var enablePvp: Boolean = _
  private var preventBlockBreak: Boolean = _
  private var difficulty: Int = _
  private var gametype: String = _
  private var gamerules: JsonObject = _

  def setId(id: String): MappackBuilder = {this.id = id; this}
  def setName(name: String): MappackBuilder = {this.name = name; this}
  def setWorldType(worldType: String): MappackBuilder = {this.worldType = worldType; this}
  def setSpawnAnimals(spawnAnimals: Boolean): MappackBuilder = {this.spawnAnimals = spawnAnimals; this}
  def setSpawnMonsters(spawnMonsters: Boolean): MappackBuilder = {this.spawnMonsters = spawnMonsters; this}
  def setMapFile(mapFile: FileUpload): MappackBuilder = {this.mapFile = mapFile; this}
  def setWorldSource(worldSource: String): MappackBuilder = {this.worldSource = worldSource; this}
  def setGameMode(gameMode: Int): MappackBuilder = {this.gameMode = gameMode; this}
  def setEnablePvp(enablePvp: Boolean): MappackBuilder = {this.enablePvp = enablePvp; this}
  def setPreventBlockBreak(preventBlockBreak: Boolean): MappackBuilder = {this.preventBlockBreak = preventBlockBreak; this}
  def setDifficulty(difficulty: Int): MappackBuilder = {this.difficulty = difficulty; this}
  def setGametype(gametype: String): MappackBuilder = {this.gametype = gametype; this}
  def setGamerules(gamerules: JsonObject): MappackBuilder = {this.gamerules = gamerules; this}

  def build(): Mappack = {
    val mappack = new Mappack(this.id, this.name)
    mappack
  }
}
