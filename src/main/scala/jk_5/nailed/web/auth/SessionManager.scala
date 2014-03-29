package jk_5.nailed.web.auth

import scala.collection.mutable
import jk_5.nailed.web.NailedWeb
import java.util.concurrent.Callable
import com.lambdaworks.crypto.SCryptUtil

/**
 * No description given
 *
 * @author jk-5
 */
object SessionManager {

  private final val sessions = mutable.HashSet[AuthSession]()

  @inline def checkPassword(user: User, password: String) = NailedWeb.worker.submit(new CheckSCryptHashTask(password, user.getPasswordHash)).get()

  def getSession(user: User, password: String): Option[AuthSession] = {
    if(this.checkPassword(user, password)){
      val session = new AuthSession(user.getID)
      this.sessions.add(session)
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

  def dropSession(session: AuthSession): Boolean = this.sessions.remove(session)
}

class CreateSCryptHashTask(private final val input: String, private final val cpuCost: Int = 16384, private final val memoryCost: Int = 8, private final val parallel: Int = 1) extends Callable[String] {
  def call(): String = SCryptUtil.scrypt(this.input, this.cpuCost, this.memoryCost, this.parallel)
}
class CheckSCryptHashTask(private final val input: String, private final val hash: String) extends Callable[Boolean] {
  def call(): Boolean = SCryptUtil.check(this.input, this.hash)
}
