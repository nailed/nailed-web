package jk_5.nailed.web.webserver.http.routing.parameterized

import jk_5.nailed.web.webserver.http.routing.{RequestHandler, RouteBuilder}
import io.netty.handler.codec.http.HttpMethod
import scala.collection.mutable
import scala.collection.immutable.{HashMap, HashSet}
import jk_5.nailed.web.webserver.http.routing.url.UrlPattern

/**
 * No description given
 *
 * @author jk-5
 */
class ParameterizedRouteBuilder(_uri: String, _handler: RequestHandler) extends RouteBuilder(_uri, _handler) {

  private val aliases = mutable.ListBuffer[String]()

  override protected def newRoute(pattern: String, handler: RequestHandler, method: HttpMethod, name: String, flags: HashSet[String], parameters: HashMap[String, Any], baseUrl: String) = {
    val r = new ParameterizedRoute(new UrlPattern(pattern), handler, method, name, flags, parameters, baseUrl)
    r.addAliases(aliases.toList)
    r
  }

  override def toRegexPattern(uri: String) = {
    var pattern = uri
    if(pattern != null && !pattern.startsWith("/")){
      pattern = "/" + pattern
    }
    pattern
  }

  /**
   * Associate another URI pattern to this route, essentially making an alias
   * for the route. There may be multiple alias URIs for a given route. Note
   * that new parameter nodes (e.g. {id}) in the URI will be available within
   * the method. Parameter nodes that are missing from the alias will not be
   * available in the action method.
   *
   * @param uri the alias URI.
   * @return the ParameterizedRouteBuilder instance (this).
   */
  def alias(uri: String) = {
    this.aliases += uri
    this
  }
}
