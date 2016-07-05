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

import java.util.concurrent.TimeUnit

import org.openqa.selenium.WebDriver

import scala.concurrent.duration.FiniteDuration

trait DriverDsl {
  /**
   * Sets the amount of time the driver should wait when searching for an element that is not immediately present.
   *
   * <p>
   * When searching for requested elements, Selenium will poll the page until the requested element
   * (or at least one of multiple requested elements) is found or this "implicit wait" timeout has expired.
   * If the timeout expires, Selenium will throw <code>NoSuchElementException</code>,
   * which will wrap by <code>TestHelper.failTest()</code>.
   * </p>
   *
   * <p>
   * It is recommended to use PageObject's <code>waitFor</code> construct instead.
   * </p>
   *
   * <p>
   * This method invokes <code>manage.timeouts.implicitlyWait</code> on the passed <code>WebDriver</code>.
   * See the documentation of Selenium's
   * <code>WebDriver#Timeouts</code> interface for more information.
   * </p>
   *
   * @param timeout the time span to implicitly wait
   *
   * @param driver the <code>WebDriver</code> on which to set the implicit wait
   */
  protected def implicitlyWait(timeout: FiniteDuration)(implicit driver: WebDriver): Unit = {
    driver.manage.timeouts.implicitlyWait(timeout.toNanos, TimeUnit.NANOSECONDS)
  }

  /**
   * Close all windows, and exit the driver.
   *
   * @param driver the <code>WebDriver</code> on which to quit.
   */
  protected def quit()(implicit driver: WebDriver): Unit = {
    driver.quit()
  }
}
