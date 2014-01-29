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

package jk_5.nailed.web.config

import java.io.PrintWriter

/**
 * No description given
 *
 * @author jk-5
 */
class ConfigTag(var parent: ConfigTagParent, var name: String) extends ConfigTagParent {

  var position = Int.MaxValue
  var brace = false
  var value: String = _
  var qualifiedName = parent.getNameQualifier + name
  var newline = parent.newlinemode == 2
  this.parent.addChild(this)

  override def getNameQualifier = this.qualifiedName + "."
  def saveConfig() = parent.saveConfig()
  def onLoaded = this
  def setValue(value: String){
    this.value = value
    this.saveConfig()
  }
  def setDefaultValue(defaultvalue: String) {
    if (value == null) {
      value = defaultvalue
      this.saveConfig()
    }
  }
  def setIntValue(i: Int) = setValue(i.toString)
  def setBooleanValue(b: Boolean) = setValue(b.toString)
  def setHexValue(i: Int) = setValue("0x" + (i.toLong << 32 >>> 32, 16).toString())

  def getValue = this.value
  def getValue(defaultvalue: String): String = {
    setDefaultValue(defaultvalue)
    value
  }
  def getIntValue = this.getValue.toInt
  def getIntValue(defaultvalue: Int): Int = {
    if (value == null) this.setIntValue(defaultvalue)
    try this.getIntValue
    catch {
      case nfe: NumberFormatException => {
        setIntValue(defaultvalue)
        return getIntValue
      }
    }
  }
  def getBooleanValue: Boolean = {
    val value: String = getValue
    if (value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"))) return true
    else if (value != null && (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no"))) return false
    throw new NumberFormatException(this.qualifiedName + ".value=" + value)
  }
  def getBooleanValue(default: Boolean): Boolean = {
    if (value == null) this.setBooleanValue(default)
    try getBooleanValue
    catch {
      case nfe: NumberFormatException => {
        setBooleanValue(default)
        return getBooleanValue
      }
    }
  }

  def save(writer: PrintWriter, tabs: Int, bracequalifier: String, first: Boolean) {
    var vname: String = null
    if (this.qualifiedName.contains(".") && bracequalifier.length > 0) vname = this.qualifiedName.substring(bracequalifier.length + 1)
    else vname = this.qualifiedName
    if (newline && !first) ConfigFile.writeLine(writer, "", tabs)
    writeComment(writer, tabs)
    if (value != null) ConfigFile.writeLine(writer, vname + "=" + value, tabs)
    if (!hasChildTags) return
    if (brace) {
      if (value == null) ConfigFile.writeLine(writer, vname, tabs)
      ConfigFile.writeLine(writer, "{", tabs)
      saveTagTree(writer, tabs + 1, this.qualifiedName)
      ConfigFile.writeLine(writer, "}", tabs)
    }else{
      saveTagTree(writer, tabs, bracequalifier)
    }
  }

  override def setComment(comment: String): ConfigTag = {
    super.setComment(comment)
    this
  }

  override def setSortMode(mode: Int): ConfigTag = {
    super.setSortMode(mode)
    this
  }

  def setNewLine(b: Boolean): ConfigTag = {
    newline = b
    this.saveConfig()
    this
  }

  def useBraces: ConfigTag = {
    brace = true
    if (parent.newlinemode == 1) newline = true
    this.saveConfig()
    this
  }

  def setPosition(pos: Int): ConfigTag = {
    position = pos
    this.saveConfig()
    this
  }

  override def containsTag(tagname: String) = getTag(tagname, create = false) != null
}
