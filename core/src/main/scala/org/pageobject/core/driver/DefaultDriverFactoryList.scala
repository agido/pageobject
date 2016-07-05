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
import org.pageobject.core.tools.OS

/**
 * A abstract class to simply create a local DriverFactory.
 *
 * Only the browser name (used for the test name) and a webDriver create function is needed.
 *
 * @param name used as test group name
 *
 * @param create used to create a webDriver for each test
 */
abstract class DefaultCreateDriverFactory(val name: String, val create: () => WebDriver) extends DynamicDriverFactory {
  protected def createWebDriver(): WebDriver = create()
}

/**
 * A DriverFactory creating a local HtmlUnit browser
 */
case class HtmlUnitDriverFactory() extends DefaultCreateDriverFactory("HtmlUnit", () => new HtmlUnitDriver())

/**
 * A DriverFactory creating a local Firefox browser
 */
case class FirefoxDriverFactory() extends DefaultCreateDriverFactory("Firefox", () => new FirefoxDriver())

/**
 * A DriverFactory creating a local Safari browser
 */
case class SafariDriverFactory() extends DefaultCreateDriverFactory("Safari", () => new SafariDriver()) {
  override val compatible = OS.isOSX
}

/**
 * A DriverFactory creating a local Chrome browser
 */
case class ChromeDriverFactory() extends DefaultCreateDriverFactory("Chrome", () => new ChromeDriver())

/**
 * A DriverFactory creating a local Internet Explorer browser
 */
case class InternetExplorerDriverFactory()
  extends DefaultCreateDriverFactory("InternetExplorer", () => new InternetExplorerDriver()) {

  override val compatible = OS.isWindows
}

/**
 * A list of DriverFactories to create all supported local browsers
 *
 * DriverFactoryList will filter DriverFactories returning false for compatible or selected
 */
class DefaultDriverFactoryList extends DriverFactoryList(HtmlUnitDriverFactory(), FirefoxDriverFactory(),
  SafariDriverFactory(), ChromeDriverFactory(), InternetExplorerDriverFactory())
