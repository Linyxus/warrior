package Scareq

import org.jsoup.Connection.Method
import org.jsoup.Connection

trait JsoupSyntax {
  extension (conn: Connection) {
    def toPost: Connection = conn.method(Method.POST)

    def toGet: Connection = conn.method(Method.GET)

    def humanize(userAgent: String, referrer: String) =
      conn
        .userAgent(userAgent)
        .referrer(referrer)
        .followRedirects(true)
        .ignoreContentType(true)
  }
}

object JsoupSyntax extends JsoupSyntax
