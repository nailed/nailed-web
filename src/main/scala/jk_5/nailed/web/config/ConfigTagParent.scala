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

import java.util.{Collections, Comparator}
import java.util
import scala.collection.JavaConversions._
import java.io.{PrintWriter, IOException, BufferedReader}

/**
 * No description given
 *
 * @author jk-5
 */
object ConfigTagParent {
  class TagOrderComparator(private final val sortMode: Int) extends Comparator[ConfigTag] {
    def compare(o1: ConfigTag, o2: ConfigTag): Int =
      if(o1.position != o2.position) this.compareInt(o1.position, o2.position)
      else if(o1.brace != o2.brace) if(o1.brace) 1 else -1
      else this.sortMode match{
        case 1 =>
          if(o1.value == o2.value) 0
          else if(o1.value == null) 1
          else if(o2.value == null) -1
          else o1.value.compareTo(o2.value)
        case _ => o1.name.compareTo(o2.name)
      }
    private def compareInt(a: Int, b: Int) = if(a == b) 0 else if(a < b) -1 else 1
  }
}

abstract class ConfigTagParent {

  private val childtags = new util.TreeMap[String, ConfigTag]()
  var comment: String = _
  var sortMode = 0        // 0 = sort by name, 1 = sort by value
  var newlinemode = 1     // 0 = never newlines, 1 = newlines when braced, 2 = always newlines

  def saveConfig()
  def getNameQualifier: String
  def setComment(comment: String): ConfigTagParent = {
    this.comment = comment
    this.saveConfig()
    this
  }
  def setSortMode(mode: Int): ConfigTagParent = {
    this.sortMode = mode
    this.saveConfig()
    this
  }
  def setNewLineMode(mode: Int): ConfigTagParent = {
    this.newlinemode = mode
    for(entry <- this.childtags.entrySet()){
      val tag = entry.getValue
      if(this.newlinemode == 0) tag.newline = false
      else if(this.newlinemode == 1) tag.newline = tag.brace
      else if(this.newlinemode == 2) tag.newline = true
    }
    this.saveConfig()
    this
  }
  def childTagMap = this.childtags
  def hasChildTags = !childtags.isEmpty
  def containsTag(tagname: String) = this.getTag(tagname, create = false) != null
  def getNewTag(tagname: String) = new ConfigTag(this, tagname)
  def getTag(tagname: String, create: Boolean): ConfigTag = {
    val dotpos = tagname.indexOf(".")
    val basetagname = if (dotpos == -1) tagname else tagname.substring(0, dotpos)
    var basetag = childtags.get(basetagname)
    if (basetag == null) {
      if (!create) return null
      basetag = getNewTag(basetagname)
      saveConfig()
    }
    if (dotpos == -1) return basetag
    basetag.getTag(tagname.substring(dotpos + 1), create)
  }
  def getTag(tagname: String): ConfigTag = this.getTag(tagname, create = true)
  def removeTag(tagname: String): Boolean = {
    val tag = this.getTag(tagname, create = false)
    if (tag == null) return false
    val dotpos = tagname.lastIndexOf(".")
    val lastpart = if (dotpos == -1) tagname else tagname.substring(dotpos + 1, tagname.length)
    if (tag.parent != null) {
      val ret = tag.parent.childtags.remove(lastpart) != null
      if (ret) saveConfig()
      return ret
    }
    false
  }
  def addChild(tag: ConfigTag) = this.childtags.put(tag.name, tag)
  def getSortedTagList: util.List[ConfigTag] = {
    val taglist = new util.ArrayList[ConfigTag](childtags.size)
    for (tag <- childtags.entrySet) taglist.add(tag.getValue)
    Collections.sort(taglist, new ConfigTagParent.TagOrderComparator(sortMode))
    taglist
  }

  def loadChildren(reader: BufferedReader) {
    var comment: String = ""
    var bracequalifier: String = ""
    try {
      var loop = true
      while (loop) {
        val line = ConfigFile.readLine(reader)
        if(line == null) loop = false
        else{
          if (line.startsWith("#")){
            if (comment == null || (comment == "")) comment = line.substring(1)
            else comment = comment + "\n" + line.substring(1)
          }else if (line.contains("=")) {
            val qualifiedname: String = line.substring(0, line.indexOf("="))
            getTag(qualifiedname).onLoaded.setComment(comment).setValue(line.substring(line.indexOf("=") + 1))
            comment = ""
            bracequalifier = qualifiedname
          }else if (line == "{") {
            getTag(bracequalifier).setComment(comment).useBraces.loadChildren(reader)
            comment = ""
            bracequalifier = ""
          }else if (line == "}") {
            loop = false
          }else bracequalifier = line
        }
      }
    }catch {
      case e: IOException => throw new RuntimeException(e)
    }
  }

  def saveTagTree(writer: PrintWriter, tabs: Int, bracequalifier: String) {
    var first = true
    for (tag <- getSortedTagList) {
      tag.save(writer, tabs, bracequalifier, first)
      first = false
    }
  }
  def writeComment(writer: PrintWriter, tabs: Int){
    if(comment != null && !(comment == "")) {
      val comments: Array[String] = comment.split("\n")
      for (i <- 0 until comments.length) ConfigFile.writeLine(writer, "#" + comments(i), tabs)
    }
  }
}
