package jk_5.nailed.web.webserver.http.routing

import scala.collection.mutable
import jk_5.nailed.web.webserver.http.routing.parameterized.ParameterizedRouteBuilder

/**
 * No description given
 *
 * @author jk-5
 */
class RouteDeclaration {

  private val routeBuilders = mutable.ArrayBuffer[RouteBuilder]()

  def uri(uri: String, handler: RequestHandler) = {
    val builder = new ParameterizedRouteBuilder(uri, handler)
    routeBuilders += builder
    builder
  }

  /*def regex(regex: String, handler: RequestHandler) = {
    val builder = new RegexRouteBuilder(regex, handler)
    routeBuilders += builder
    builder
  }*/

  def createMapping() = {
    val mapping = new RouteMapping
    for(builder <- routeBuilders){
      for(route <- builder.build()){
        mapping.addRoute(route)
      }
    }
    mapping
  }
}
