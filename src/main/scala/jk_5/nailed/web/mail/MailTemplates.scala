package jk_5.nailed.web.mail

import java.io.File
import scala.io.Source
import scala.collection.mutable
import jk_5.nailed.web.webserver.http.handlers.WebServerHandlerHtml

/**
 * No description given
 *
 * @author jk-5
 */
object MailTemplates {

  val parseInto = "$parseInto{"
  val include = "$include{"
  val content = "$content"

  def readFile(file: File): String = {
    val f = Source.fromFile(file)("utf-8")
    val lines = f.getLines().mkString("\n")
    f.close()
    lines
  }

  def parseTemplate(name: String, vars: mutable.Map[String, String] = mutable.HashMap()): String = {
    var file = this.readFile(new File(WebServerHandlerHtml.htdocsLocation + "/mailtemplate/" + name))
    var iterate = true
    var prevIndex = -1
    while(iterate){
      val index = file.indexOf(include, prevIndex)
      val end = file.indexOf("}", index)
      if(index == -1 || end == -1) iterate = false
      else{
        val dataBefore = file.substring(0, index)
        val dataAfter = file.substring(end + 1)
        file = dataBefore + this.parseTemplate(file.substring(index + include.length, end)) + dataAfter
        prevIndex = end
      }
    }
    if(file.startsWith(parseInto)){
      val i = file.indexOf("}")
      val wrapper = this.parseTemplate(file.substring(parseInto.length, i))
      file = file.substring(i + 1).trim //Strip the $parseInto{} block
      //file.replaceFirst("\r|\n|\r\n", "") //Also strip the first newline that was behind $parseInto{}
      file = wrapper.replace(content, file)
    }
    vars.foreach(v => file = file.replace("${" + v._1 + "}", v._2))
    file
  }
}
