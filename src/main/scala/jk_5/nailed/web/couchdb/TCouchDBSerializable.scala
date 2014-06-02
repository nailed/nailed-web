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

import jk_5.jsonlibrary.JsonObject
import org.apache.logging.log4j.LogManager

/**
 * No description given
 *
 * @author jk-5
 */
trait TCouchDBSerializable {

  private var _databaseType: String = {
    val ann = this.getClass.getAnnotation(classOf[DatabaseType])
    if(ann != null) ann.value() else this.getClass.getSimpleName
  }
  private var _databaseId: UID = null
  private var _databaseRevision: String = null
  private var _existsInDatabase: Boolean = false
  private lazy val _cdblogger = LogManager.getLogger

  protected final def setDatabaseType(typ: String) = this._databaseType = typ

  private final def writeDB(data: JsonObject){
    if(this._existsInDatabase) data.add("_id", this._databaseId.toString)
    if(this._existsInDatabase) data.add("_rev", this._databaseRevision)
    data.add("type", this._databaseType)
    this.writeToJsonForDB(data)
  }
  final def readDB(data: JsonObject){
    if(this._databaseId == null) this._databaseId = new UID(data.get("_id").asString)
    this._databaseRevision = data.get("_rev").asString
    this._existsInDatabase = true
    this.readFromJsonForDB(data)
  }

  protected def writeToJsonForDB(data: JsonObject)
  protected def readFromJsonForDB(data: JsonObject)

  def saveToDatabase(){
    if(this._databaseId == null) this._databaseId = CouchDB.newID
    val data = new JsonObject
    this.writeDB(data)
    CouchDB.updateDocument(this._databaseId, data){
      response => {
        val resData = JsonObject.readFrom(response)
        if(resData.get("ok") != null && resData.get("ok").asBoolean){
          this._databaseRevision = resData.get("rev").asString
          this._existsInDatabase = true
        }else{
          _cdblogger.warn("Error (" + resData.get("error").asString + ") while saving document " + this.toString + " to CouchDB")
          _cdblogger.warn(resData.get("reason").asString)
        }
      }
    }
  }

  def refreshFromDatabase(){
    CouchDB.getDocument(this._databaseId){
      response => {
        val resData = JsonObject.readFrom(response)
        this.readDB(resData)
        this._existsInDatabase = true
      }
    }
  }

  final def getID = this._databaseId
  private [couchdb] final def setID(id: UID){
    if(this._databaseId != null) throw new IllegalStateException("The database id is already set!")
    this._databaseId = id
  }
}
