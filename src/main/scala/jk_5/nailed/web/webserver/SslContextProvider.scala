package jk_5.nailed.web.webserver

import javax.net.ssl.{KeyManagerFactory, SSLContext}
import java.security.KeyStore
import java.io.FileInputStream
import org.apache.logging.log4j.{MarkerManager, LogManager}
import scala.util.Properties

/**
 * No description given
 *
 * @author jk-5
 */
object SslContextProvider {

  private var serverContext: SSLContext = _
  private var valid = false

  private val logger = LogManager.getLogger
  private val marker = MarkerManager.getMarker("SSL")

  def load(){
    val algorithm = Properties.propOrElse("ssl.KeyManagerFactory.algorithm", "SunX509")
    val keyStoreFilePath = Properties.propOrEmpty("keystore.file.path")
    val keyStoreFilePassword = Properties.propOrEmpty("keystore.file.password")
    if(!keyStoreFilePath.isEmpty && !keyStoreFilePassword.isEmpty) {
      try {
        val ks = KeyStore.getInstance("JKS")
        val fin = new FileInputStream(keyStoreFilePath)
        ks.load(fin, keyStoreFilePassword.toCharArray)
        val kmf = KeyManagerFactory.getInstance(algorithm)
        kmf.init(ks, keyStoreFilePassword.toCharArray)
        this.serverContext = SSLContext.getInstance("TLS")
        this.serverContext.init(kmf.getKeyManagers, null, null)
        this.valid = true
      } catch {
        case e: Exception =>
      }
    }
    if(this.valid) {
      this.logger.info(this.marker, "Valid SSL certificate was found")
    } else {
      this.logger.info(this.marker, "No valid SSL certificate was found")
    }
  }

  def getContext = this.serverContext
  def isValid = this.valid
}
