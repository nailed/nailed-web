package jk_5.nailed.web.webserver.http.routing

import jk_5.nailed.web.webserver.http.routing.url.UrlMatcher
import io.netty.handler.codec.http.HttpMethod
import scala.collection.immutable.{HashMap, HashSet}

/**
 * No description given
 *
 * @author jk-5
 */
case class Route(
  private val matcher: UrlMatcher,
  private val handler: RequestHandler,
  private val method: HttpMethod,
  private val name: String,
  private val flags: HashSet[String],
  private val parameters: HashMap[String, Any],
  private val baseUrl: String
) {

  def isFlagged(flag: String) = flags.contains(flag)
  def getParameter(name: String) = this.parameters.get(name)
  @inline def hasParameter(name: String) = this.getParameter(name).isDefined
  def getHandler = this.handler
  def getMethod = this.method
  def getName = this.name
  def hasName = this.name != null
  def getBaseUrl = if(this.baseUrl == null) "" else this.baseUrl
  def getFullPattern = getBaseUrl + getPattern
  def getPattern = matcher.getPattern
  def doMatch(url: String) = matcher.doMatch(url)
  def getUrlParameters = matcher.getParameterNames

  def invoke(request: Request, response: Response){
    try{
      method match {
        case HttpMethod.GET => handler.handleGet(request, response)
        case HttpMethod.POST => handler.handlePost(request, response)
        case HttpMethod.PUT => handler.handlePut(request, response)
        case HttpMethod.PATCH => handler.handlePatch(request, response)
        case HttpMethod.HEAD => handler.handleHead(request, response)
        case HttpMethod.CONNECT => handler.handleConnect(request, response)
        case HttpMethod.DELETE => handler.handleDelete(request, response)
        case HttpMethod.OPTIONS => handler.handleOptions(request, response)
        case HttpMethod.TRACE => handler.handleTrace(request, response)
        case _ => //Better be safe than sorry
      }
    }catch{
      case e: Exception => //TODO: Throw something that triggers a 500
    }
  }
}
