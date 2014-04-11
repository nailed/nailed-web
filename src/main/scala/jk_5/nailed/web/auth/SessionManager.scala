package jk_5.nailed.web.auth

import scala.collection.mutable
import jk_5.nailed.web.NailedWeb
import java.util.concurrent.{TimeUnit, Callable}
import com.lambdaworks.crypto.SCryptUtil
import org.apache.logging.log4j.{LogManager, MarkerManager}

/**
 * No description given
 *
 * @author jk-5
 */
object SessionManager {

  private final val sessions = mutable.HashSet[AuthSession]()
  private[auth] final val logger = LogManager.getLogger
  private[auth] final val marker = MarkerManager.getMarker("Sessions")

  @inline def checkPassword(user: User, password: String) = NailedWeb.worker.submit(new CheckSCryptHashTask(password, user.getPasswordHash)).get()

  def getSession(user: User, password: String): Option[AuthSession] = {
    if(this.checkPassword(user, password)){
      val session = new AuthSession(user.getID)
      this.sessions.add(session)
      SessionManager.logger.debug(SessionManager.marker, "User {} authenticated (SessionID {})", user.getFullName, session.getID)
      return Some(session)
    }
    None
  }

  def getSession(secret: String): Option[AuthSession] = {
    this.sessions.find(_.getID.toString == secret)
  }

  def dropSession(secret: String): Boolean = {
    val session = this.sessions.find(_.getID.toString == secret)
    if(session.isDefined){
      this.dropSession(session.get)
    }else false
  }

  def dropSession(session: AuthSession): Boolean = {
    SessionManager.logger.debug(SessionManager.marker, "Dropping session {} owned by {}", session.getID, session.getUser.getFullName)
    this.sessions.remove(session)
  }
}

class CreateSCryptHashTask(private final val input: String, private final val cpuCost: Int = 16384, private final val memoryCost: Int = 8, private final val parallel: Int = 1) extends Callable[String] {
  def call(): String = {
    SessionManager.logger.debug(SessionManager.marker, "Creating SCrypt hash")
    val start = System.nanoTime()
    val ret = SCryptUtil.scrypt(this.input, this.cpuCost, this.memoryCost, this.parallel)
    val mean = System.nanoTime() - start
    SessionManager.logger.debug(SessionManager.marker, "Finished creating SCrypt hash (Took {}ms)", TimeUnit.NANOSECONDS.toMillis(mean).toString)
    ret
  }
}
class CheckSCryptHashTask(private final val input: String, private final val hash: String) extends Callable[Boolean] {
  def call(): Boolean = {
    SessionManager.logger.debug(SessionManager.marker, "Checking SCrypt hash")
    val start = System.nanoTime()
    val ret = SCryptUtil.check(this.input, this.hash)
    val mean = System.nanoTime() - start
    SessionManager.logger.debug(SessionManager.marker, "Finished checking SCrypt hash (Took {}ms) (Returned {})", TimeUnit.NANOSECONDS.toMillis(mean).toString, if(ret) "true" else "false")
    ret
  }
}
