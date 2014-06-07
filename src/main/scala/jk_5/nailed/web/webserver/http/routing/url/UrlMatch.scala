package jk_5.nailed.web.webserver.http.routing.url

import scala.collection.immutable

/**
 * No description given
 *
 * @author jk-5
 */
class UrlMatch(private val parameters: immutable.HashMap[String, String] = immutable.HashMap[String, String]()) {

  def get(name: String) = parameters.get(name)
  def parameterSet() = parameters.toSet
}
