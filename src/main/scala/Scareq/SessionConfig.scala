package Scareq

enum ReferrerMode {
  case Always(url: String)
  case Follow(start: String)
}

case class SessionConfig(userAgent: String, referrerMode: ReferrerMode)

object SessionConfig {
  private val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36"

  val defaultConfig: SessionConfig = SessionConfig(userAgent, ReferrerMode.Follow("https://www.baidu.com"))
}
