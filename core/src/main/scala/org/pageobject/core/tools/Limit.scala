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

import com.typesafe.config.ConfigFactory

import scala.util.Try

/**
 * Support for running multiple browsers in parallel.
 *
 * Can be set by environment variable or with typesafe config under the path org.pageobject.<name>-limit
 *
 * Overall test limit:
 * Use TEST_LIMIT=-1 to allow unlimited parallel test runs
 * The default TEST_LIMIT is 1, use TEST_LIMIT=4 to allow 4 tests to be executed at the same time.
 *
 * Browser test limit:
 * When not configured, a limit of 1 is assumed if the browser driver is configured.
 * Use 0 to disable a Browser.
 * Use -1 to allow an unlimited count for the given Browser type.
 *
 * Example:
 * FIREFOX_LIMIT=0
 * ->Do not start Firefox tests
 *
 * Example:
 * FIREFOX_LIMIT=3
 * CHROME_LIMIT=5
 * TEST_LIMIT=5
 * -> Start up to 5 tests, but no more then 3 Firefox Tests at the same time.
 *
 * Only Browsers that are supported for the platform where the tests are executed will be started.
 * E.g. you don't need to disable Internet Explorer or Safari on Linux
 */
object Limit {

  case class Limit(name: String,
                   propertyName: Option[String] = None,
                   defaultOption: Option[Int] = None,
                   isBrowser: Boolean = true) {
    // name of the environment variable used to override the limit
    def envName: String = s"${propertyName.getOrElse(name).toUpperCase}_LIMIT"

    // int value of the environment variable or None
    def env: Option[Int] = Util.parseInt {
      val configPath = s"org.pageobject.${propertyName.getOrElse(name).toLowerCase}-limit"
      sys.env.get(envName)
        .orElse(Try(ConfigFactory.load().getString(configPath)).toOption)
    }

    // true if a matching webdriver property is set
    def hasWebDriverProp: Boolean = {
      isBrowser && sys.props.keys.exists(_.startsWith(s"webdriver.${propertyName.getOrElse(name).toLowerCase}"))
    }

    // how many instances should normally be started
    def default: Int = {
      if (hasWebDriverProp) {
        1
      } else {
        defaultOption.getOrElse(0)
      }
    }

    // how many instances should be started
    def get: Int = {
      env.getOrElse(default)
    }

    // true if browser should be used
    def selected: Boolean = get != 0
  }

  /**
   * How many Browsers should be used concurrently.
   *
   * Set environment variable TEST_LIMIT to change this setting.
   *
   * Default is TEST_LIMIT=1, do not run tests in parallel
   * Set to TEST_LIMIT=n with n > 1 to allow parallel test runs.
   * Set to TEST_LIMIT=-1 to allow unlimited parallel tests.
   */
  object TestLimit extends Limit("test", defaultOption = Some(1), isBrowser = false)

  /**
   * How may HtmlUnit instances should be used concurrently.
   *
   * You need to add HtmlUnit to the classpath to be able to use it.
   *
   * Set HTMLUNIT_LIMIT=n with n > 1 to run more then one instance at the same time.
   * Set HTMLUNIT_LIMIT=-1 to allow a unlimited number of instances.
   *
   * Default is HTMLUNIT_LIMIT=0, do not run tests using HtmlUnit.
   *
   * You may also need to adjust TEST_LIMIT.
   */
  object HtmlUnitLimit extends Limit("HtmlUnit")

  /**
   * How may Firefox instances should be used concurrently.
   *
   * Add property -Dwebdriver.firefox.bin=... to tell pageobject how to start firefox.
   * See https://github.com/SeleniumHQ/selenium/wiki/FirefoxDriver
   *
   * Set FIREFOX_LIMIT=n with n > 1 to run more then one instance at the same time.
   * Set FIREFOX_LIMIT=-1 to allow a unlimited number of instances.
   *
   * Default is FIREFOX_LIMIT=1 if -Dwebdriver.firefox.bin is set, FIREFOX_LIMIT=0 otherwise.
   *
   * You may also need to adjust TEST_LIMIT.
   */
  object FirefoxLimit extends Limit("Firefox")

  /**
   * How may Chrome instances should be used concurrently.
   *
   * Add property -Dwebdriver.chrome.driver=... to tell pageobject how to start chrome.
   * See https://github.com/SeleniumHQ/selenium/wiki/ChromeDriver
   *
   * Set CHROME_LIMIT=n with n > 1 to run more then one instance at the same time.
   * Set CHROME_LIMIT=-1 to allow a unlimited number of instances.
   *
   * Default is CHROME_LIMIT=1 if -Dwebdriver.chrome.driver is set, CHROME_LIMIT=0 otherwise.
   *
   * You may also need to adjust TEST_LIMIT.
   */
  object ChromeLimit extends Limit("Chrome")

  /**
   * How may Safari instances should be used concurrently.
   * Safari is only supported on osx and will be ignored on other platforms.
   *
   * Add property -Dwebdriver.safari.driver=... to tell pageobject how to start safari.
   * See https://github.com/SeleniumHQ/selenium/wiki/SafariDriver.
   *
   * Set SAFARI_LIMIT=n with n > 1 to run more then one instance at the same time.
   * Set SAFARI_LIMIT=-1 to allow a unlimited number of instances.
   *
   * Default is SAFARI_LIMIT=1 if -Dwebdriver.safari.driver is set, SAFARI_LIMIT=0 otherwise.
   *
   * You may also need to adjust TEST_LIMIT.
   */
  object SafariLimit extends Limit("Safari")

  /**
   * How may Internet Explorer instances should be used concurrently.
   * Internet Explorer is only supported on windows and will be ignored on other platforms.
   *
   * Add property -Dwebdriver.ie.driver=... to tell pageobject how to start internet explorer.
   * See https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver
   *
   * Set IE_LIMIT=n with n > 1 to run more then one instance at the same time.
   * Set IE_LIMIT=-1 to allow a unlimited number of instances.
   *
   * default is IE_LIMIT=1 if -Dwebdriver.ie.driver is set, IE_LIMIT=0 otherwise.
   *
   * You may also need to adjust TEST_LIMIT.
   */
  object InternetExplorerLimit extends Limit("InternetExplorer", propertyName = Some("ie"))

}

trait LimitProvider {
  def limit: Limit.Limit
}
