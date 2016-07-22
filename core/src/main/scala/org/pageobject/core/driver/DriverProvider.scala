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
import org.pageobject.core.TestHelper

import org.pageobject.core.tools.DynamicOptionVariable

/**
 * To implicit access the webDriver, mainly needed by <code>LocatorDsl</code>
 * just extend your class with <code>DriverProvider</code>
 *
 * Example:
 * <code>
 *   class Example extends FunSpec with WebBrowser with DriverLauncher {
 *     // ...
 *   }
 * </code>
 */
trait DriverProvider {
  protected[pageobject] implicit def webDriver: WebDriver
}

/**
 * Default implementation for DriverProvider storing the DriverFactory in DriverFactoryHolder
 */
trait DefaultDriverProvider extends DriverProvider {
  protected[pageobject] implicit def webDriver: WebDriver = {
    val err = "No WebDriver found. Forgot to extend your test with DriverLauncher?"
    DriverFactoryHolder.option.getOrElse(TestHelper.notAllowed(err)).webDriver
  }
}

/**
 * To prevent boilerplate code the active driver factory is stored here.
 *
 * <code>DriverLauncherWrapper</code> will store the factory here while instantiating the suite.
 *
 * The suite must be of type <code>DriverLauncher with Suite</code>.
 *
 * <code>DriverLauncher</code> will store the factory for later usage.
 *
 * When <code>runTest</code> and <code>runTests</code> of the suite are launched,
 * the previous saved driver factory will be stored here again.
 */
private[pageobject] object DriverFactoryHolder extends DynamicOptionVariable[DriverFactory]()
