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

/**
 * This trait is part of the PageObject DSL.
 *
 * This trait implements page title and page source access.
 */
trait PageDsl {
  /**
   * Returns the title of the current page, or the empty string if the current page has no title.
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   *
   * @return the current page's title, or the empty string if the current page has no title
   */
  protected def pageTitle(implicit driver: WebDriver): String = {
    Option(driver.getTitle).getOrElse("")
  }

  /**
   * Returns the source of the current page.
   *
   * <p>
   * This method invokes <code>getPageSource</code> on the passed <code>WebDriver</code> and returns the result.
   * </p>
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   *
   * @return the source of the current page
   */
  protected def pageSource(implicit driver: WebDriver): String = driver.getPageSource
}
