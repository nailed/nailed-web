/*
 * Copyright 2013 TeamNexus
 *
 * TeamNexus Licenses this file to you under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License
 */

package jk_5.nailed.web.couchdb

import com.ning.http.client.{ListenableFuture, RequestBuilder, AsyncHttpClient, Response}
import io.netty.handler.codec.http.HttpHeaders
import jk_5.jsonlibrary.JsonObject
import jk_5.commons.config.ConfigTag

/**
 * No description given
 *
 * @author jk-5
 */
object CouchDB {
  private var serverHostname: String = _
  private var serverPort: Int = _
  private var databaseName: String = _
  private var ssl: Boolean = false

  private final val httpClient = new AsyncHttpClient()

  def newID = UID.randomUID

  def readConfig(tag: ConfigTag){
    tag.setUseBraces(useBraces = true).setComment("Database options")
    this.serverHostname = tag.get("hostname").setComment("The IP/Address for the couchdb server").asString("localhost")
    this.serverPort = tag.get("port").setComment("The port for the couchdb server").asInt(5984)
    this.databaseName = tag.get("name").setComment("The name of the couchdb database that Nailed will use").asString("nailed")
    this.ssl = tag.get("ssl").setComment("Should we use SSL?").asBoolean(default = false)
  }

  def getObjectFromID[T <: TCouchDBSerializable](id: UID, obj: T): T = {
    obj.setID(id)
    obj.refreshFromDatabase()
    obj
  }

  def updateDocument(id: UID, data: JsonObject): ListenableFuture[Response] = {
    assert(id != null)
    assert(data != null)
    val builder = new RequestBuilder("PUT")
    builder.setUrl((if(this.ssl) "https://" else "http://") + this.serverHostname + ":" + this.serverPort + "/" + this.databaseName + "/" + id.toString)
    builder.setHeader(HttpHeaders.Names.CONTENT_TYPE.toString, "application/json")
    builder.setBody(data.stringify)
    val request = builder.build()
    this.httpClient.executeRequest(request)
  }

  def getDocument(id: UID): ListenableFuture[Response] = {
    val builder = new RequestBuilder("GET")
    builder.setUrl((if(this.ssl) "https://" else "http://") + this.serverHostname + ":" + this.serverPort + "/" + this.databaseName + "/" + id.toString)
    val request = builder.build()
    this.httpClient.executeRequest(request)
  }

  def getViewData(viewGroup: String, viewName: String): ListenableFuture[Response] = {
    val builder = new RequestBuilder("GET")
    builder.setUrl((if(this.ssl) "https://" else "http://") + this.serverHostname + ":" + this.serverPort + "/" + this.databaseName + "/_design/" + viewGroup + "/_view/" + viewName)
    val request = builder.build()
    this.httpClient.executeRequest(request)
  }
}
