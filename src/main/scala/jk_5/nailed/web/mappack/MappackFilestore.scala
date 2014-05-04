package jk_5.nailed.web.mappack

import jk_5.jsonlibrary.{JsonObject, JsonArray}
import java.io.{BufferedInputStream, FileInputStream, File}
import java.security.MessageDigest
import scala.collection.mutable
import java.nio.file.{Paths, Files}

/**
 * No description given
 *
 * @author jk-5
 */
object MappackFilestore {
  val root = new File("mappacks")
  val objectRoot = new File(root, "objects")

  root.mkdir()
  objectRoot.mkdir()

  def hash(file: File): String = {
    val bis = new BufferedInputStream(new FileInputStream(file))
    val hash = this.hash(Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray)
    bis.close()
    hash
  }

  def hash(bytes: Array[Byte]): String = {
    val hasher = MessageDigest.getInstance("SHA1")
    hasher.update(bytes)
    val hash = hasher.digest()
    var result = ""
    for(i <- 0 until hash.length) result += Integer.toString((hash(i) & 0xff) + 0x100, 16).substring(1)
    result
  }

  def copy(src: File, dest: File){
    Files.copy(Paths.get(src.getAbsolutePath), Paths.get(dest.getAbsolutePath))
  }
}

class MappackFilestore(val owner: Mappack) {

  private val files = mutable.ArrayBuffer[MappackFile]()

  def addFile(mpf: MappackFile) = files += mpf
  def addFile(file: File, path: String){
    val mpf = new MappackFile(file, path)
    this.addFile(mpf)
    val f = mpf.getFile
    if(f.exists() && f.length() == mpf.size){
      //We're fine
      return
    }else{
      f.getCanonicalFile.getParentFile.mkdirs()
      MappackFilestore.copy(file, f)
    }
  }

  def addDir(file: File, path: String = ""){
    file.listFiles().foreach(f => {
      if(f.isDirectory) addFile(f, if(path.isEmpty) f.getName else path + "/" + f.getName)
      else{
        this.addFile(f, if(path.isEmpty) f.getName else path + "/" + f.getName)
      }
    })
  }

  def readData(data: JsonArray){
    data.getValues.map(_.asObject).foreach(o => files += new MappackFile(o))
  }

  def toJson: JsonArray = {
    val ret = new JsonArray
    files.foreach(f => ret.add(f.toJson))
    ret
  }

  def getFile(hash: String) = this.files.find(_.hash == hash)
}

case class MappackFile(path: String, hash: String, size: Long) {
  def this(data: JsonObject) = this(data.get("path").asString, data.get("hash").asString, data.get("size").asLong)
  def this(file: File, path: String) = this(path, MappackFilestore.hash(file), file.length())

  private var file: Option[File] = None

  def toJson = new JsonObject().add("path", path).add("hash", hash).add("size", size)
  def getFile: File = {
    if(this.file.isEmpty) this.file = Some(new File(MappackFilestore.objectRoot, this.hash.substring(0, 2) + "/" + this.hash))
    this.file.get
  }
}
