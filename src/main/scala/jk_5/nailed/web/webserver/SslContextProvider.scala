package jk_5.nailed.web.webserver

import javax.net.ssl.{KeyManagerFactory, SSLContext}
import java.security.{KeyStore, Security}
import java.io.FileInputStream
import org.apache.logging.log4j.{MarkerManager, LogManager}

/**
 * No description given
 *
 * @author jk-5
 */
object SslContextProvider {

  private final val PROTOCOL = "TLS"
  private var serverContext: SSLContext = _
  private var valid = false

  private val logger = LogManager.getLogger
  private val marker = MarkerManager.getMarker("SSL")

  def load(){
    try{
      var algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm")
      if(algorithm == null) algorithm = "SunX509"
      try{
        val keyStoreFilePath = System.getProperty("keystore.file.path")
        val keyStoreFilePassword = System.getProperty("keystore.file.password")
        val ks = KeyStore.getInstance("JKS")
        val fin = new FileInputStream(keyStoreFilePath)
        ks.load(fin, keyStoreFilePassword.toCharArray)
        val kmf = KeyManagerFactory.getInstance(algorithm)
        kmf.init(ks, keyStoreFilePassword.toCharArray)
        this.serverContext = SSLContext.getInstance(PROTOCOL)
        this.serverContext.init(kmf.getKeyManagers, null, null)
        this.valid = true
      }catch{case e: Exception =>}
    }catch{case e: Exception =>}

    if(this.valid) {
      this.logger.info(this.marker, "Valid SSL certificate was found")
    }else{
      this.logger.info(this.marker, "No valid SSL certificate was found")
    }
  }

  def getContext = this.serverContext
  def isValid = this.valid
}
