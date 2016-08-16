package org.pageobject.core.tools

import org.pageobject.core.browser.PageHolder
import org.pageobject.core.page.ActivePage
import org.pageobject.core.page.PageModule
import org.pageobject.core.page.PageObject

trait CookieDumper extends ActivePage {
  this: PageObject =>

  def logCookie(cookie: String): Unit

  private object cookies extends PageModule {
    def get(): Seq[String] = {
      all.cookies.map(_.toString)
    }
  }

  override def onActivated(pageHolder: PageHolder): Unit = {
    cookies.get().foreach(logCookie)
  }
}

trait CookieLogger extends CookieDumper {
  this: PageObject with Logging =>

  def cookieLogLevel: LogLevel

  def logCookie(cookie: String): Unit = log(cookieLogLevel, s"Cookie: $cookie")
}

trait DebugCookieLogger extends CookieLogger {
  this: PageObject with Logging =>

  override def cookieLogLevel = LogLevel.Debug
}

trait InfoCookieLogger extends CookieLogger {
  this: PageObject with Logging =>

  override def cookieLogLevel = LogLevel.Info
}
