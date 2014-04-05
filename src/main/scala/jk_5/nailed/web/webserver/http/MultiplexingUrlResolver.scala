package jk_5.nailed.web.webserver.http

import scala.collection.mutable
import io.netty.channel.ChannelHandler
import java.util.regex.Pattern
import jk_5.nailed.web.webserver.UrlEscaper

/**
 * No description given
 *
 * @author jk-5
 */
class MultiplexingUrlResolver {

  private final val handlers = mutable.LinkedHashMap[Pattern, Class[_ <: ChannelHandler]]()
  def addHandler(pattern: String, handler: Class[_ <: ChannelHandler]) = this.handlers.put(Pattern.compile(pattern), handler)

  def getValueForURL(url: String, recursive: Boolean = false): Option[URLData] = {
    val args = mutable.HashMap[String, String]()
    var breakIterator = false
    var path = UrlEscaper.sanitizeURI(url.split("\\?", 2)(0))
    for((regex, handler) <- this.handlers if !breakIterator){
      val matcher = regex.matcher(path)
      if(matcher.find()){
        var i = 1
        var breakLoop = false
        while(!breakLoop) try{
          val res = path.replaceAll(regex.pattern(), "$" + i)
          if(res.equals("$" + i)) breakLoop = true
          args.put("part" + i, res)
          i += 1
        }catch{
          case e: IndexOutOfBoundsException => breakLoop = true
        }
        path = matcher.group()
        breakIterator = true
        return Some(new URLData(regex.pattern(), url, args, handler))
      }
    }
    if(!recursive) return this.getValueForURL(if(url.endsWith("/")) url.substring(0, url.length - 1) else url, recursive = true)
    None
  }
}
