package jk_5.nailed.web.mappack

import io.netty.handler.codec.http.multipart.FileUpload
import jk_5.jsonlibrary.JsonObject
import java.io.{FileOutputStream, BufferedInputStream, File}
import java.util.zip.ZipFile
import jk_5.nailed.web.NailedWeb

/**
 * No description given
 *
 * @author jk-5
 */
object MappackBuilder {
  val uploaded = new File("uploaded/mappacks")
  uploaded.mkdirs()
}

class MappackBuilder {

  private var id: String = _
  private var name: String = _
  private var worldType: String = _
  private var mapFile: File = _
  private var worldSource: String = _
  private var gameMode: Int = _
  private var enablePvp: Boolean = _
  private var preventBlockBreak: Boolean = _
  private var difficulty: Int = _
  private var gametype: String = _
  private val spawns = new SpawnRules
  private val gamerules = new GameRules

  def setId(id: String): MappackBuilder = {this.id = id; this}
  def setName(name: String): MappackBuilder = {this.name = name; this}
  def setWorldType(worldType: String): MappackBuilder = {this.worldType = worldType; this}
  def setMapFile(mapFile: FileUpload): MappackBuilder = {
    this.mapFile = new File(MappackBuilder.uploaded, mapFile.getFilename)
    mapFile.renameTo(this.mapFile)
    this
  }
  def setWorldSource(worldSource: String): MappackBuilder = {this.worldSource = worldSource; this}
  def setGameMode(gameMode: Int): MappackBuilder = {this.gameMode = gameMode; this}
  def setEnablePvp(enablePvp: Boolean): MappackBuilder = {this.enablePvp = enablePvp; this}
  def setPreventBlockBreak(preventBlockBreak: Boolean): MappackBuilder = {this.preventBlockBreak = preventBlockBreak; this}
  def setDifficulty(difficulty: Int): MappackBuilder = {this.difficulty = difficulty; this}
  def setGametype(gametype: String): MappackBuilder = {this.gametype = gametype; this}
  def setGamerules(gamerules: JsonObject): MappackBuilder = {this.gamerules.read(gamerules); this}
  def setSpawns(spawns: JsonObject): MappackBuilder = {this.spawns.read(spawns); this}

  def build(callback: MappackBuildCallback){
    val cb = Option(callback)
    val mappack = new Mappack(id, name)
    mappack.spawnRules.from(this.spawns)
    mappack.gameRules.from(this.gamerules)
    mappack.difficulty = this.difficulty
    mappack.defaultGamemode = this.gameMode
    mappack.preventBlockBreak = this.preventBlockBreak
    if(mapFile == null){
      mappack.saveToDatabase()
      cb.foreach(_.onDone(mappack))
      return
    }
    NailedWeb.worker.execute(new Runnable(){
      override def run(){
        val zipFile = new ZipFile(mapFile)
        val e = zipFile.entries()
        while(e.hasMoreElements){
          val entry = e.nextElement()
          if(!entry.isDirectory && !entry.getName.contains("##MCEDIT.TEMP##") && !entry.getName.startsWith("__MACOSX/") && !entry.getName.endsWith(".DS_Store")){
            //I don't care about size limits here. I just read the entire file into the ram and hash it
            val is = new BufferedInputStream(zipFile.getInputStream(entry))
            val buffer = new Array[Byte](entry.getSize.toInt)
            is.read(buffer)
            is.close()
            val hash = MappackFilestore.hash(buffer)
            val mpf = new MappackFile(entry.getName, hash, buffer.length)
            mappack.filestore.addFile(mpf)
            val f = mpf.getFile
            if(!(f.exists() && f.length() == mpf.size)){
              f.getCanonicalFile.getParentFile.mkdirs()
              val fos = new FileOutputStream(f)
              fos.write(buffer)
              fos.close()
            }
          }
        }
        mapFile.delete()
        mapFile = null
        mappack.saveToDatabase()
        cb.foreach(_.onDone(mappack))
      }
    })
  }
}

trait MappackBuildCallback {
  def onDone(mappack: Mappack)
  def onError(description: String)
}
