package Scareq

import scala.collection.JavaConverters._
import scala.collection.mutable
import org.jsoup.{Jsoup, Connection}
import org.jsoup.Connection.{Response, Method}

import JsoupSyntax._

class Session(val config: SessionConfig = SessionConfig.defaultConfig) {
  private val myCookies: mutable.Map[String, String] = mutable.Map.empty

  def cookies: Map[String, String] = myCookies.toMap

  private def updateCookies(setCookies: Map[String, String]): Map[String, String] = {
    myCookies ++= setCookies.toList
    cookies
  }

  private var prevUrl: Option[String] = None

  def location: Option[String] = prevUrl

  private def getReferrer: String = config.referrerMode match {
    case ReferrerMode.Always(url) => url
    case ReferrerMode.Follow(start) => prevUrl getOrElse start
  }

  private def connect(url: String): Connection = {
    val conn = Jsoup.connect(url).humanize(config.userAgent, getReferrer)
    prevUrl = Some(url)
    conn
  }

  /** Perform a connection. Properly use and update current cookies.
    *
    * @param conn The connection to perform.
    */
  private def performConnection(conn: Connection): Response = {
    val resp = conn.cookies(cookies.asJava).execute

    updateCookies(resp.cookies.asScala.toMap)

    resp
  }

  def request(url: String, method: Connection.Method = Method.GET, datas: List[(String, String)] = Nil, headers: List[(String, String)] = Nil): Response = {
    def builder[X](func: (Connection, X) => Connection)(conn: Connection, xs: List[X]): Connection = {
      @annotation.tailrec def recur(conn: Connection, xs: List[X]): Connection = xs match {
        case Nil => conn
        case x :: xs => recur(func(conn, x), xs)
      }

      recur(conn, xs)
    }

    val buildData = builder[(String, String)] { (conn, x) =>
      conn.data(x._1, x._2)
    }

    val buildHeader = builder[(String, String)] { (conn, x) =>
      conn.header(x._1, x._2)
    }

    performConnection(buildData(buildHeader(connect(url), headers), datas).method(method))
  }

  def get(url: String, data: List[(String, String)] = Nil, headers: List[(String, String)] = Nil): Response =
    request(url, Method.GET, data, headers)

  def post(url: String, data: List[(String, String)] = Nil, headers: List[(String, String)] = Nil): Response =
    request(url, Method.POST, data, headers)
}

