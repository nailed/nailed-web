package jk_5.nailed.web.webserver.http.routing.url

import java.util.regex.{Matcher, Pattern}
import scala.collection.{mutable, immutable}

/**
 * No description given
 *
 * @author jk-5
 */
object UrlRegex {
  private final val PARAMETER_PREFIX = "regexGroup"
}

class UrlRegex(private val pattern: Pattern) extends UrlMatcher {
  def this(regex: String) = this(Pattern.compile(regex))

  override def getPattern = pattern.pattern()
  override def getParameterNames = null
  override def doMatch(url: String): Option[UrlMatch] = {
    val matcher = pattern.matcher(url)
    if(matcher.matches()) Some(new UrlMatch(extractParameters(matcher)))
    else None
  }
  override def matches(url: String) = doMatch(url).isDefined

  def extractParameters(matcher: Matcher): immutable.HashMap[String, String] = {
    val values = mutable.HashMap[String, String]()
    for(i <- 0 until matcher.groupCount()){
      val value = matcher.group(i + 1)
      if(value != null){
        values.put(UrlRegex.PARAMETER_PREFIX + i, value)
      }
    }
    immutable.HashMap(values.toSeq: _*)
  }
}
