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
package org.pageobject.scalatest

import org.pageobject.core.browser.PageBrowser
import org.pageobject.core.driver.DefaultDriverProvider
import org.scalatest.Suite

/**
 * When you decide to work with the PageObject pattern this trait is a most common customized adaption. It
 * summarizes the traits needed for using the PageObject pattern. In detail:
 * You need a PageBrowser to support the navigation, to work with WaitFor and PageHolder. The former allows
 * to wait for events for a certain time, the latter administrates the internal web driver or element access.
 * You need a DriverLauncher which starts the web driver. In this case you might use different browsers if needed.
 * You need a DefaultDriverProvider to administrate the DriverLauncher.
 * Last but not least you need BrowserLimitSuite to support the execution on multiple browsers in parallel.
 * So this is the collection of traits needed to write a Page Object Suite.
 *
 * If you want to customize some parts you can extend your Suite
 * with some of the trais listed here and replace others by a custom implementation.
 */
trait PageObjectSuite extends PageBrowser with DriverLauncher with DefaultDriverProvider
  with ConfigureableParallelTestLimit {
  this: Suite =>
}
