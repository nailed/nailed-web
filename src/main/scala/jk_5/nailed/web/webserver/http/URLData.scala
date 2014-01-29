package jk_5.nailed.web.webserver.http

import scala.collection.mutable
import io.netty.channel.ChannelHandler

/**
 * No description given
 *
 * @author jk-5
 */
case class URLData(private val pattern: String, private val realUrl: String, private val parameters: mutable.HashMap[String, String], private val handler: Class[_ <: ChannelHandler] = null){
  @inline def getPattern = this.pattern
  @inline def getURL = this.realUrl
  @inline def getParameters = this.parameters
  @inline def getHandler = this.handler
}
