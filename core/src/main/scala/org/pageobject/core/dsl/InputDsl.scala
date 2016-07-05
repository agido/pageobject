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
import org.openqa.selenium.WebElement
import org.pageobject.core.TestHelper

import scala.util.control.NonFatal

trait InputDsl extends SwitchTargetDsl {
  /**
   * Submit the form where current active element belongs to,
   * and throws TestFailedException if current active element is not in a form
   * or underlying WebDriver encounters problem when submitting the form.
   *
   * If this causes the current page to change, this call will block until the new page is loaded.
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   */
  protected def submit()(implicit driver: WebDriver): Unit = {
    try {
      switch to activeElement submit()
    } catch {
      case e: org.openqa.selenium.NoSuchElementException =>
        TestHelper.failTest("Current element is not a form element.")
      case NonFatal(e) =>
        // Could happens as bug in different WebDriver,
        // like NullPointerException in HtmlUnitDriver when element is not a form element.
        // Anyway, we'll just wrap them as TestFailedException
        TestHelper.failTest(s"WebDriver encountered problem to submit(): ${e.getMessage}")
    }
  }

  // Clears the text field or area, then presses the passed keys
  /**
   * Clears the current active <code>TextField</code> or <code>TextArea</code>, and presses the passed keys.
   * Throws <code>TestFailedException</code> if current active is not <code>TextField</code> or <code>TextArea</code>.
   *
   * @param value keys to press in current active <code>TextField</code> or <code>TextArea</code>
   */
  protected def enter(value: String)(implicit driver: WebDriver): Unit = {
    val element = switch to activeElement
    element.value = value
  }

  /**
   * Press the passed keys to current active element.
   *
   * @param value keys to press in current active element
   */
  protected def pressKeys(value: String)(implicit driver: WebDriver): Unit = {
    val ae: WebElement = driver.switchTo.activeElement
    ae.sendKeys(value)
  }
}
