package jk_5.nailed.web.webserver.http.routing.parameterized

import jk_5.nailed.web.webserver.http.routing.{RequestHandler, Route}
import io.netty.handler.codec.http.HttpMethod
import scala.collection.immutable.{HashMap, HashSet}
import jk_5.nailed.web.webserver.http.routing.url.{UrlMatch, UrlPattern}

/**
 * No description given
 *
 * @author jk-5
 */
class ParameterizedRoute(pattern: UrlPattern, handler: RequestHandler, method: HttpMethod, name: String, flags: HashSet[String], parameters: HashMap[String, Any], baseUrl: String) extends Route(pattern, handler, method, name, flags, parameters, baseUrl) {

  private var aliases: Array[UrlPattern] = _

  def addAliases(urls: List[String]){
    if(urls == null) return
    aliases = new Array[UrlPattern](urls.size)
    var i = -1
    for(url <- urls){
      aliases({i += 1; i}) = new UrlPattern(url)
    }
  }

  override def doMatch(url: String): Option[UrlMatch] = {
    val m = super.doMatch(url)
    if(m.isEmpty && aliases != null){
      for(alias <- aliases){
        val mat = alias.doMatch(url)
        if(mat.isDefined) return mat
      }
    }
    m
  }
}
