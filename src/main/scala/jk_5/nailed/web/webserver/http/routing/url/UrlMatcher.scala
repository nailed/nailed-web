package jk_5.nailed.web.webserver.http.routing.url

/**
 * No description given
 *
 * @author jk-5
 */
trait UrlMatcher {

  def matches(url: String): Boolean
  def doMatch(url: String): Option[UrlMatch]
  def getPattern: String
  def getParameterNames: List[String]
}
