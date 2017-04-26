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
package org.pageobject.core.driver.vnc

import org.pageobject.core.driver.DriverFactoryList
import org.pageobject.core.driver.FixedLocation
import org.pageobject.core.driver.LoggingDriverFactory
import org.pageobject.core.driver.RemoteDriverFactory
import org.pageobject.core.driver.TakeScreenshot
import org.pageobject.core.driver.ThreadNameNumberingDriverFactory
import org.pageobject.core.driver.TracedRemoteDriverFactory

/**
 * Traits used by all VncDriverFactories
 */
trait DefaultVncDriverTraits extends FixedLocation with TakeScreenshot with TracedRemoteDriverFactory
  with LoggingDriverFactory with ThreadNameNumberingDriverFactory {

  this: RemoteDriverFactory =>
}

/**
 * This DriverFactory will launch a VNC Server, start the selenium server inside
 * and the creates a Chrome Browser with matching RemoteWebDriver to connect into the VNC Server.
 */
object DefaultVncChromeDriverFactory extends VncChromeDriverFactory(DefaultVncServerManager) with DefaultVncDriverTraits

/**
 * This DriverFactory will launch a VNC Server, start the selenium server inside
 * and the creates a Firefox Browser with matching RemoteWebDriver to connect into the VNC Server.
 */
object DefaultVncFirefoxDriverFactory extends VncFirefoxDriverFactory(DefaultVncServerManager)
  with DefaultVncDriverTraits
