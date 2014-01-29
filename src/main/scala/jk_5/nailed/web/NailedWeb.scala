package jk_5.nailed.web

import jk_5.nailed.web.webserver.{MimeTypesLookup, WebServer}

/**
 * No description given
 *
 * @author jk-5
 */
object NailedWeb {

  val version = "0.1-SNAPSHOT"

  def main(args: Array[String]){
    MimeTypesLookup.load()
    WebServer.start()
  }
}
