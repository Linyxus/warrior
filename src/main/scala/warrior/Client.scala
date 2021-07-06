package warrior

import Scareq.Session

/** Interface for student management system clients.
  */
abstract class Client {
  /** Checks whether the client has been online.
    */
  def isOnline: Boolean

  /** Login the system.
    */
  def login: Unit

  /** Scareq session used by this client.
    */
  def session: Session

  /** Get cookies of the session used by this client.
    */
  def cookies: Map[String, String] = session.cookies

  /** Ensure that this client has been online, then perform the action.
    */
  def ensureOnline[T](body: => T): T =
    if !isOnline then login
    body
}

/** An implementation of clients over vpn.
  */
class VpnClient(val username: String, private val vpnPassword: String, private val password: String) extends Client {
  private var myIsOnline = false
  private val mySession = new Session

  private val baseUrl = "https://webvpn.bupt.edu.cn/https/77726476706e69737468656265737421fae0469069327d406a468ca88d1b203b"

  private def fullUrl(path: String): String = baseUrl ++ path

  override def isOnline = myIsOnline

  override def session = mySession

  override def login =
    loginVpn
    loginSystem

  /** Logs into the vpn system.
    */
  private def loginVpn: Unit = {
    session.get("https://webvpn.bupt.edu.cn/login")

    val headers2 = List(
      "Content-Type" -> "application/x-www-form-urlencoded; charset=UTF-8"
    )
    val data2 = List(
      "auth_type" -> "local",
      "username" -> username,
      "password" -> vpnPassword,
      "needCaptcha" -> "false",
    )
    val resp2 = session.post("https://webvpn.bupt.edu.cn/do-login", data = data2, headers = headers2)

    session.get("https://webvpn.bupt.edu.cn")
  }

  /** Logins into the management system.
    */
  private def loginSystem: Unit = ???
}

