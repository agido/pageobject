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
package org.pageobject.core

import org.pageobject.core.dsl.BrowserControlDsl
import org.pageobject.core.page.PageModule
import org.pageobject.core.page.PageObject

/**
 * This is a PageObject to detect Browser Error Pages.
 *
 * It is used as a default for UnexpectedPages to cancel a test when a BrowserErrorPage was found.
 */
case class BrowserErrorPage() extends PageObject {

  object content extends PageModule with BrowserControlDsl {
    private def isFirefoxErrorPage = $(cssSelector("link[href=\"chrome://browser/skin/aboutNetError.css\"]"))
    private def isChromeErrorPage = $(id("main-frame-error"))

    def isBrowserErrorPage: Boolean = {
      val url = currentUrl
      // chrome e.g. https://www.google.com:80/
      url == "" ||
        // chrome e.g. http://localhost:65534/
        url == "data:text/html,chromewebdata" ||
        isChromeErrorPage.anyDisplayed ||
        // firefox
        isFirefoxErrorPage.anyDisplayed ||
        url.startsWith("about:neterror")
    }
  }

  override def atChecker() = content.isBrowserErrorPage
}
