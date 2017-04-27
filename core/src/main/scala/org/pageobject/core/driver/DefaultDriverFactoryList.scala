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
package org.pageobject.core.driver

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.safari.SafariDriver
import org.pageobject.core.driver.vnc.DefaultVncChromeDriverFactory
import org.pageobject.core.driver.vnc.DefaultVncFirefoxDriverFactory
import org.pageobject.core.tools.Limit.ChromeLimit
import org.pageobject.core.tools.Limit.FirefoxLimit
import org.pageobject.core.tools.Limit.HtmlUnitLimit
import org.pageobject.core.tools.Limit.InternetExplorerLimit
import org.pageobject.core.tools.Limit.Limit
import org.pageobject.core.tools.Limit.SafariLimit
import org.pageobject.core.tools.OS

/**
 * A abstract class to simply create a local DriverFactory.
 *
 * Only the browser limit and a webDriver create function is needed.
 *
 * @param limit used as test group name and to detect how many instances should be started
 *
 * @param create used to create a webDriver for each test
 */
abstract class DefaultCreateDriverFactory(val limit: Limit, val create: () => WebDriver) extends DynamicDriverFactory {
  protected def createRealWebDriver(): WebDriver = create()
}

/**
 * A DriverFactory creating a local HtmlUnit browser
 */
case object HtmlUnitDriverFactory extends DefaultCreateDriverFactory(HtmlUnitLimit, () => new HtmlUnitDriver())

/**
 * A DriverFactory creating a local Firefox browser
 */
case object FirefoxDriverFactory extends DefaultCreateDriverFactory(FirefoxLimit, () => new FirefoxDriver())

/**
 * A DriverFactory creating a local Safari browser
 */
case object SafariDriverFactory extends DefaultCreateDriverFactory(SafariLimit, () => new SafariDriver()) {
  override val compatible = OS.isOSX
}

/**
 * A DriverFactory creating a local Chrome browser
 */
case object ChromeDriverFactory extends DefaultCreateDriverFactory(ChromeLimit, () => new ChromeDriver()) {
  sys.env.get("PAGEOBJECT_CHROMEDRIVER_PATH")
    .map(path => System.setProperty("webdriver.chrome.driver", s"${path}_${OS.suffix}"))
}

/**
 * A DriverFactory creating a local Internet Explorer browser
 */
case object InternetExplorerDriverFactory
  extends DefaultCreateDriverFactory(InternetExplorerLimit, () => new InternetExplorerDriver()) {

  override val compatible = OS.isWindows
}

/**
 * A list of DriverFactories to create all supported local browsers
 *
 * DriverFactoryList will filter DriverFactories returning false for compatible or selected
 */
class DefaultDriverFactoryList extends DriverFactoryList(HtmlUnitDriverFactory, FirefoxDriverFactory,
  SafariDriverFactory, ChromeDriverFactory, InternetExplorerDriverFactory, DefaultVncChromeDriverFactory, DefaultVncFirefoxDriverFactory)
