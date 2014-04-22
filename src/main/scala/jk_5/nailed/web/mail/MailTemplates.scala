package jk_5.nailed.web.mail

import java.io.File
import scala.io.Source
import jk_5.nailed.web.webserver.http.handlers.WebServerHandlerHtml

/**
 * No description given
 *
 * @author jk-5
 */
object MailTemplates {

  def readFile(file: File): String = {
    val f = Source.fromFile(file)("utf-8")
    val lines = f.getLines().mkString("\n")
    f.close()
    lines
  }

  def readTemplate(name: String): String = {
    var file = this.readFile(new File(WebServerHandlerHtml.htdocsLocation + "/mailtemplate/" + name))
    var iterate = true
    var prevIndex = -1
    val search = "$include{"
    while(iterate){
      val index = file.indexOf(search, prevIndex)
      val end = file.indexOf("}", index)
      if(index == -1 || end == -1) iterate = false
      else{
        val dataBefore = file.substring(0, index)
        val dataAfter = file.substring(end + 1)
        file = dataBefore + this.readTemplate(file.substring(index + search.length, end)) + dataAfter
        prevIndex = end
      }
    }
    file
  }
}
