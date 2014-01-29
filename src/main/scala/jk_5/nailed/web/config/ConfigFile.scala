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

/**
 * No description given
 *
 * @author jk-5
 */

import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.io.PrintWriter

object ConfigFile {

  final val lineend: Array[Byte] = Array[Byte](0xD, 0xA)

  def readLine(reader: BufferedReader): String = {
    val line: String = reader.readLine
    if (line != null) return line.replace("\t", "")
    line
  }

  def formatLine(l: String): String = {
    var line = l.replace("\t", "")
    if (line.startsWith("#")) line
    else if (line.contains("=")) {
      line = line.substring(0, line.indexOf("=")).replace(" ", "") + line.substring(line.indexOf("="))
      line
    }else {
      line = line.replace(" ", "")
      line
    }
  }

  def writeLine(writer: PrintWriter, line: String, tabs: Int) {
    for(i <- 0 until tabs) writer.print('\t')
    writer.println(line)
  }
}

class ConfigFile(var file: File) extends ConfigTagParent {

  private var loading: Boolean = false

  if(!file.exists())
    try{
      file.createNewFile()
    }catch{case e: IOException => throw new RuntimeException(e)}
  this.newlinemode = 2
  this.loadConfig()

  private def loadConfig(){
    this.loading = true
    var reader: BufferedReader = null
    try{
      reader = new BufferedReader(new FileReader(file))
      var read = true
      while (read){
        reader.mark(2000)
        val line: String = reader.readLine
        if (line != null && line.startsWith("#"))
          if (comment == null || (comment == "")) comment = line.substring(1)
          else comment = comment + "\n" + line.substring(1)
        else{
          reader.reset()
          read = false
        }
      }
      loadChildren(reader)
      reader.close()
    }catch{case e: IOException => throw new RuntimeException(e)}
    this.loading = false
  }
  override def setComment(header: String): ConfigFile = {
    super.setComment(header)
    this
  }
  override def setSortMode(mode: Int): ConfigFile = {
    super.setSortMode(mode)
    this
  }
  def getNameQualifier = ""
  def saveConfig() {
    if(this.loading) return
    var writer: PrintWriter = null
    try writer = new PrintWriter(file)
    catch {case e: FileNotFoundException => throw new RuntimeException(e)}
    writeComment(writer, 0)
    ConfigFile.writeLine(writer, "", 0)
    saveTagTree(writer, 0, "")
    writer.flush()
    writer.close()
  }
  def isLoading = this.loading
}
