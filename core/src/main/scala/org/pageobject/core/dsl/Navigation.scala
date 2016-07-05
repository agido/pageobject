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
 * This object contains helper used to implement NavigationDsl.
 */
object Navigation {

  /**
   * This object is part of PageObject DSL.
   *
   * <p>
   * This object enables syntax such as the following:
   * </p>
   *
   * <pre class="stHighlight">
   * go to "http://www.artima.com"
   * &#94;
   * </pre>
   **/
  object Go {
    /**
     * Sends the browser to the passed URL.
     *
     * <p>
     * This method enables syntax such as the following:
     * </p>
     *
     * <pre class="stHighlight">
     * go to "http://www.artima.com"
     * &#94;
     * </pre>
     *
     *
     * @param url the URL to which to send the browser
     *
     * @param driver the <code>WebDriver</code> with which to drive the browser
     */
    def to(url: String)(implicit driver: WebDriver): Unit = {
      driver.get(url)
    }

    /**
     * Sends the browser to the URL contained in the passed <code>Page</code> object.
     *
     * <p>
     * This method enables syntax such as the following:
     * </p>
     *
     * <pre class="stHighlight">
     * go to homePage
     * &#94;
     * </pre>
     *
     *
     * @param page the <code>Page</code> object containing the URL to which to send the browser
     *
     * @param driver the <code>WebDriver</code> with which to drive the browser
     */
    def to(page: UrlPage)(implicit driver: WebDriver): Unit = {
      driver.get(page.url)
    }
  }

}
