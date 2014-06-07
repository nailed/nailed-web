package jk_5.nailed.web.webserver.http.routing

import scala.collection.{mutable, immutable}
import io.netty.handler.codec.http.HttpMethod

/**
 * No description given
 *
 * @author jk-5
 */
class RouteMapping {

  private final val deleteRoutes = mutable.ArrayBuffer[Route]()
  private final val getRoutes = mutable.ArrayBuffer[Route]()
  private final val postRoutes = mutable.ArrayBuffer[Route]()
  private final val putRoutes = mutable.ArrayBuffer[Route]()
  private final val optionRoutes = mutable.ArrayBuffer[Route]()
  private final val headRoutes = mutable.ArrayBuffer[Route]()
  private final val routes = immutable.HashMap[HttpMethod, mutable.ArrayBuffer[Route]](
    HttpMethod.DELETE -> deleteRoutes,
    HttpMethod.GET -> getRoutes,
    HttpMethod.POST -> postRoutes,
    HttpMethod.PUT -> putRoutes,
    HttpMethod.OPTIONS -> optionRoutes,
    HttpMethod.HEAD -> headRoutes
  )
  private final val routesByName = mutable.HashMap[String, mutable.HashMap[HttpMethod, Route]]()
  private final val routesByPattern = mutable.LinkedHashMap[String, mutable.ArrayBuffer[Route]]()

  def addRoute(route: Route){
    routes.get(route.getMethod).get += route
    addByPattern(route)
    if(route.hasName) addNamedRoute(route)
  }

  private def addByPattern(route: Route){
    var routes = routesByPattern.get(route.getPattern)
    if(routes.isEmpty){
      routes = Some(mutable.ArrayBuffer[Route]())
      routesByPattern.put(route.getPattern, routes.get)
    }
    routes.get += route
  }

  private def addNamedRoute(route: Route){
    var routes = routesByName.get(route.getName)
    if(routes.isEmpty){
      routes = Some(mutable.HashMap[HttpMethod, Route]())
      routesByName.put(route.getName, routes.get)
    }
    routes.get.put(route.getMethod, route)
  }

  def getRoutesFor(method: HttpMethod) = {
    val r = routes.get(method)
    if(r.isEmpty) List.empty else r.toList
  }

  def getMatchingRoutes(path: String) = {
    val r = routesByPattern.values.find(_(0).doMatch(path).isDefined)
    if(r.isDefined) r.get.toList else List.empty
  }

  def getAllowedMethods(path: String) = {
    val r = getMatchingRoutes(path)
    if(r.isEmpty) List.empty else r.map(_.getMethod)
  }

  def getNamedRoute(name: String, method: HttpMethod) = {
    val r = routesByName.get(name)
    if(r.isEmpty) None else r.get.get(method)
  }
}
