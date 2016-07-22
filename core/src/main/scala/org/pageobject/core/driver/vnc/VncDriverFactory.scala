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

import java.util.concurrent.TimeUnit

import org.openqa.selenium.Capabilities
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.pageobject.core.driver.RemoteDriverFactory

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration
import org.pageobject.core.tools.DynamicOptionVariable

/**
 * A WebDriverFactory used to connect to a selenium server running inside of a VNC Server.
 *
 * A VncServerManager is used to create and release a VncServer.
 *
 * @param name the name of the browser to launch
 *
 * @param vncServerManager manager used to create and release a VncServer
 *
 * @tparam V type of the VncServer to use, normally DefaultVncServer
 */
abstract class VncDriverFactory[V <: SeleniumVncServer](val name: String, vncServerManager: VncServerManager[V])
  extends RemoteDriverFactory {

  private object vncServer extends DynamicOptionVariable[V]()

  protected def url(): String = vncServer.value.url

  protected def vncServerDuration() = FiniteDuration(1, TimeUnit.MINUTES)

  protected def awaitVncServer(): V = {
    Await.result(vncServerManager.getOrCreateFreeVncServer(), vncServerDuration())
  }

  override def runTest[T](testName: String, fn: => T): T = {
    val vnc: V = awaitVncServer()
    try vncServer.withValue(Some(vnc)) {
      super.runTest(testName, fn)
    } finally {
      vncServerManager.release(vnc)
    }
  }
}

/**
 * A WebDriverFactory used to start Chrome inside of a VNC Server.
 *
 * @param vncServerManager manager used to create and release a VncServer
 *
 * @tparam V type of the VncServer to use, normally DefaultVncServer
 */
case class VncChromeDriverFactory[V <: SeleniumVncServer](vncServerManager: VncServerManager[V])
  extends VncDriverFactory("chrome", vncServerManager) {

  def capabilities(): Capabilities = {
    val capabilities = DesiredCapabilities.chrome()
    val options = new ChromeOptions()
    options.addArguments("ignore-certificate-errors")
    capabilities.setCapability(ChromeOptions.CAPABILITY, options)
    capabilities
  }
}

/**
 * A WebDriverFactory used to start Firefox inside of a VNC Server.
 *
 * @param vncServerManager manager used to create and release a VncServer
 *
 * @tparam V type of the VncServer to use, normally DefaultVncServer
 */
case class VncFirefoxDriverFactory[V <: SeleniumVncServer](vncServerManager: VncServerManager[V])
  extends VncDriverFactory("firefox", vncServerManager) {

  def capabilities(): Capabilities = {
    DesiredCapabilities.firefox()
  }
}
