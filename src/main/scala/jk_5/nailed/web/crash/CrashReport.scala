package jk_5.nailed.web.crash

import jk_5.jsonlibrary.JsonObject
import jk_5.nailed.web.couchdb.{DatabaseType, TCouchDBSerializable}

/**
 * No description given
 *
 * @author jk-5
 */
@DatabaseType("crashreport")
case class CrashReport(stacktrace: String, data: JsonObject) extends TCouchDBSerializable {
  override protected def writeToJsonForDB(data: JsonObject): Unit = ???

  override protected def readFromJsonForDB(data: JsonObject): Unit = ???
}
