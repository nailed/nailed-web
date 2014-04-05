package jk_5.nailed.web

import jk_5.nailed.web.webserver.{MimeTypesLookup, WebServer}
import java.util.concurrent.Executors
import jk_5.nailed.web.couchdb.CouchDB
import java.io.File
import org.apache.logging.log4j.LogManager
import jk_5.commons.config.ConfigFile

/**
 * No description given
 *
 * @author jk-5
 */
object NailedWeb {

  val version = "0.1-SNAPSHOT"

  final val worker = Executors.newCachedThreadPool()

  private final val CONFIG_DIR = new File("config")
  private var config: ConfigFile = null

  private final val logger = LogManager.getLogger

  def main(args: Array[String]){
    this.logger.info("Starting Server")
    this.logger.info("Reading Config")
    if(!this.CONFIG_DIR.exists()) this.CONFIG_DIR.mkdirs()
    this.config = ConfigFile.fromFile(new File(this.CONFIG_DIR, "Nailed.cfg")).setComment("Nailed main configuration file")

    CouchDB.load()
    MimeTypesLookup.load()
    WebServer.start()
  }

  @inline def getConfig = this.config
}

object LogUtils {
  def mask(input: String): String = {
    val build = new StringBuilder
    for(i <- 1 to input.size) build.append("*")
    build.toString()
  }
}
