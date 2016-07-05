/*
 * Copyright 2001-2016 Artima, Inc.
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
package org.pageobject.core.dsl

import org.openqa.selenium.WebDriver
import org.pageobject.core.page.UrlPage

/**
 * This trait is part of the PageObject DSL.
 *
 * This trait implements navigation commands.
 */
trait NavigationDsl {
  protected val go = Navigation.Go

  /**
   * Sends the browser to the passed URL.
   *
   * <p>
   * Here's an example:
   * </p>
   *
   * <pre class="stHighlight">
   * goTo("http://www.artima.com")
   * </pre>
   *
   * @param url the URL to which to send the browser
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   */
  protected def goTo(url: String)(implicit driver: WebDriver): Unit = {
    go to url
  }

  /**
   * Sends the browser to the URL contained in the passed <code>Page</code> object.
   *
   * <p>
   * Here's an example:
   * </p>
   *
   * <pre class="stHighlight">
   * goTo(homePage)
   * </pre>
   *
   * @param page the <code>Page</code> object containing the URL to which to send the browser
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   */
  protected def goTo(page: UrlPage)(implicit driver: WebDriver): Unit = {
    go to page
  }

  /**
   * Closes the current browser window, and exits the driver if the current window was the only one remaining.
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   */
  protected def close()(implicit driver: WebDriver): Unit = {
    driver.close()
  }

  /**
   * Returns the URL of the current page.
   *
   * <p>
   * This method invokes <code>getCurrentUrl</code> on the passed <code>WebDriver</code> and returns the result.
   * </p>
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   *
   * @return the URL of the current page
   */
  protected def currentUrl(implicit driver: WebDriver): String = driver.getCurrentUrl

  /**
   * Go back to previous page.
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   */
  protected def goBack()(implicit driver: WebDriver): Unit = {
    driver.navigate.back()
  }

  /**
   * Go forward to next page.
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   */
  protected def goForward()(implicit driver: WebDriver): Unit = {
    driver.navigate.forward()
  }

  /**
   * Reload the current page.
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   */
  protected def reloadPage()(implicit driver: WebDriver): Unit = {
    driver.navigate.refresh()
  }
}
