package jk_5.nailed.web.mail

import jk_5.commons.config.ConfigTag
import java.util.{Date, Properties}
import javax.mail._
import javax.mail.internet.{InternetAddress, MimeMessage}
import jk_5.nailed.web.auth.User
import org.apache.logging.log4j.LogManager

/**
 * No description given
 *
 * @author jk-5
 */
object Mailer {

  private var properties: Properties = _
  private var username: String = _
  private var password: String = _

  val logger = LogManager.getLogger

  private val authenticator = new Authenticator {
    override def getPasswordAuthentication = new PasswordAuthentication(username, password)
  }

  def readConfig(tag: ConfigTag){
    tag.setUseBraces(useBraces = true)
    val port = tag.get("port").asInt(993): Integer
    this.properties = new Properties()
    this.properties.put("mail.smtp.host", tag.get("server").asString(""))
    this.properties.put("mail.smtp.socketFactory.port", port)
    this.properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
    this.properties.put("mail.smtp.auth", tag.get("useSsl").asBoolean(default = false): java.lang.Boolean)
    this.properties.put("mail.smtp.port", port)
    this.username = tag.get("username").asString("")
    this.password = tag.get("password").asString("")
  }

  def sendMail(u: User, subject: String, data: String){
    var content = data.replace("${u.fullName}", u.fullName)
    content = content.replace("${u.username}", u.username)
    content = content.replace("${u.email}", u.email)
    content = content.replace("${u.id}", u.getID.toString)
    val session = Session.getDefaultInstance(this.properties, this.authenticator)
    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress("nailed@jk-5.tk", "Nailed"))
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(u.email).asInstanceOf[Array[Address]])
    message.setSubject(subject)
    message.setSentDate(new Date)
    message.setContent(content, "text/html; charset=utf-8")
    Transport.send(message)
    logger.trace("Sent a mail to " + u.fullName)
  }
}
