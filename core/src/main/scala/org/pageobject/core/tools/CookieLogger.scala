/*
 * Copyright 2016 agido GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
