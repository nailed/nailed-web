package jk_5.nailed.web.webserver.http.routing

import io.netty.handler.codec.http.HttpMethod
import scala.collection.{mutable, immutable}

/**
 * No description given
 *
 * @author jk-5
 */
abstract class RouteBuilder(private val uri: String, private val handler: RequestHandler) {

  private var baseUrl: String = _
  private var name: String = null
  private val methods = mutable.ArrayBuffer[HttpMethod]()
  private val flags = mutable.HashSet[String]()
  private val parameters = mutable.HashMap[String, Any]()

  /**
   * Set the base URL that is associated with this route.  By default
   * the route will inherit the base URL from the `RoutingHandler` and
   * is used when retrieving the URL pattern for a route in order to create
   * a Location or other hypermedia link.
   *
   * @param baseUrl protocol://host:port to use as a base URL in links.
   * @return the RouteBuilder instance.
   */
  def baseUrl(baseUrl: String): RouteBuilder = {
    this.baseUrl = baseUrl
    this
  }

  /**
   * Defines HTTP methods that the route will support (e.g. GET, PUT, POST, DELETE, OPTIONS, HEAD).
   * This utilizes the default HTTP method to service action mapping (e.g. GET maps to read(), PUT to update(), etc.).
   *
   * @param methods the HTTP methods supported by the route.
   * @return the RouteBuilder instance.
   */
  def method(methods: HttpMethod*): RouteBuilder = {
    this.methods ++= methods.filter(!this.methods.contains(_))
    this
  }

  /**
   * Give the route a known name to facilitate retrieving the route by name.  This facilitates
   * using the route URI pattern to create Link instances via LinkUtils.asLinks().
   *
   * The name must be unique for each URI pattern.
   *
   * @param name the given name of the route for later retrieval.
   * @return the RouteBuilder instance.
   */
  def name(name: String): RouteBuilder = {
    this.name = name
    this
  }

  /**
   * Flags are boolean settings that are created at route definition time.
   * These flags can be used to pass booleans to preprocessors, handlers or postprocessors.  An example might be:
   * flag(NO_AUTHORIZATION), which might inform an authorization preprocessor to skip authorization for this route.
   *
   * @param flagValue the name of the flag.
   * @return this RouteBuilder to facilitate method chaining.
   */
  def flag(flagValue: String): RouteBuilder = {
    this.flags += flagValue
    this
  }

  /**
   * Parameters are named settings that are created at route definition time. These parameters
   * can be used to pass data to subsequent preprocessors, controllers, or postprocessors.  This is a way to pass data
   * from a route definition down to subsequent controllers, etc.  An example might be: setParameter("route", "read_foo")
   * setParameter("permission", "view_private_data"), which might inform an authorization preprocessor of what permission
   * is being requested on a given resource.
   *
   * @param name the name of the parameter.
   * @param value an object that is the parameter value.
   * @return this RouteBuilder to facilitate method chaining.
   */
  def parameter(name: String, value: Any): RouteBuilder = {
    parameters.put(name, value)
    this
  }

  /**
   * Build the Route instances.  The last step in the Builder process.
   *
   * @return a List of Route instances.
   */
  def build(): List[Route] = {
    if(methods.isEmpty) throw new InvalidRouteException("No methods are specified for this route")

    val routes = mutable.ListBuffer[Route]()
    val pattern = toRegexPattern(uri)
    for(method <- methods){
      routes += newRoute(pattern, handler, method, name, immutable.HashSet(flags.toArray: _*), immutable.HashMap(parameters.toSeq: _*), baseUrl)
    }
    routes.toList
  }

  def toRegexPattern(uri: String): String

  protected def newRoute(pattern: String, handler: RequestHandler, method: HttpMethod, name: String, flags: immutable.HashSet[String], parameters: immutable.HashMap[String, Any], baseUrl: String): Route
}
