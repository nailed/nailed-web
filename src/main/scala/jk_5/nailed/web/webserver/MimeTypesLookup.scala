package jk_5.nailed.web.webserver

import scala.collection.mutable
import scala.collection.JavaConversions._
import java.util.Properties
import java.io.File

/**
 * No description given
 *
 * @author jk-5
 */
object MimeTypesLookup {
  private final val map = mutable.HashMap[String, String]()
  private final val default = "application/octet-stream"

  def load(){
    val stream = MimeTypesLookup.getClass.getResourceAsStream("/mimetypes.cfg")
    val props = new Properties()
    props.load(stream)
    stream.close()
    props.entrySet().foreach(e => this.map.put(e.getKey.asInstanceOf[String], e.getValue.asInstanceOf[String]))
  }

  private def getExtension(name: String): Option[String] = {
    val index = name.lastIndexOf(".")
    if(index < 0) return None
    Some(name.substring(index + 1))
  }

  def getMimeType(file: File): String = this.getMimeType(file.getName)
  def getMimeType(fileName: String): String = {
    val ext = this.getExtension(fileName)
    if(ext.isEmpty || ext.get.length == 0) return this.default
    this.map.getOrElse(ext.get, this.default)
  }
}
