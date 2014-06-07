package jk_5.nailed.web.webserver.http.routing.url

import java.util.regex.{Matcher, Pattern}
import scala.collection.{mutable, immutable}

/**
 * No description given
 *
 * @author jk-5
 */
object UrlPattern {
  // Finds parameters in the URL pattern string.
  private final val URL_PARAM_REGEX = "\\{(\\w*?)\\}"

  // Replaces parameter names in the URL pattern string to match parameters in URLs.
  private final val URL_PARAM_MATCH_REGEX = "\\([%\\\\w-.\\\\~!\\$&'\\\\(\\\\)\\\\*\\\\+,;=:\\\\[\\\\]@]+?\\)"

  // Pattern to match URL pattern parameter names.
  private final val URL_PARAM_PATTERN = Pattern.compile(URL_PARAM_REGEX)

  // Finds the 'format' portion of the URL pattern string.
  private final val URL_FORMAT_REGEX = "(?:\\.\\{format\\})$"

  // Replaces the format parameter name in the URL pattern string to match the format specifier in URLs. Appended to the end of the regex string
  // when a URL pattern contains a format parameter.
  private final val URL_FORMAT_MATCH_REGEX = "(?:\\\\.\\([\\\\w%]+?\\))?"

  // Finds the query string portion within a URL. Appended to the end of the built-up regex string.
  private final val URL_QUERY_STRING_REGEX = "(?:\\?.*?)?$"
}

class UrlPattern(private val pattern: String) extends UrlMatcher {

  private val parameterNames = mutable.ListBuffer[String]()
  private var compiledUrl: Pattern = _

  override def getPattern = this.pattern.replaceFirst(UrlPattern.URL_FORMAT_REGEX, "")
  override def getParameterNames = parameterNames.toList

  override def doMatch(url: String) = {
    val matcher = compiledUrl.matcher(url)
    if(matcher.matches()) Some(new UrlMatch(extractParameters(matcher)))
    else None
  }

  override def matches(url: String) = doMatch(url).isDefined

  def compile(){
    this.acquireParameterNames()
    var parsedPattern = this.pattern.replaceFirst(UrlPattern.URL_FORMAT_REGEX, UrlPattern.URL_FORMAT_MATCH_REGEX)
    parsedPattern = parsedPattern.replaceAll(UrlPattern.URL_PARAM_REGEX, UrlPattern.URL_PARAM_MATCH_REGEX)
    this.compiledUrl = Pattern.compile(parsedPattern + UrlPattern.URL_QUERY_STRING_REGEX)
  }

  private def acquireParameterNames(){
    val m = UrlPattern.URL_PARAM_PATTERN.matcher(this.pattern)
    while(m.find()){
      this.parameterNames += m.group(1)
    }
  }

  def extractParameters(matcher: Matcher): immutable.HashMap[String, String] = {
    val values = mutable.HashMap[String, String]()
    for(i <- 0 until matcher.groupCount()){
      val value = matcher.group(i + 1)
      if(value != null){
        values.put(parameterNames(i), value)
      }
    }
    immutable.HashMap(values.toSeq: _*)
  }
}
