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

import java.net.URL

import com.google.common.collect.ImmutableMap
import org.openqa.selenium.Capabilities
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.CommandInfo
import org.openqa.selenium.remote.HttpCommandExecutor
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.UnreachableBrowserException
import org.openqa.selenium.remote.internal.ApacheHttpClient
import org.openqa.selenium.remote.internal.HttpClientFactory
import org.pageobject.core.WaitFor
import org.pageobject.core.tools.Environment

/**
 * When using this trait you can set environment variable TRACE_REMOTE_WEB_DRIVER
 * to see commands executed by the underlying RemoteWebDriver
 */
trait TracedRemoteDriverFactory {
  this: RemoteDriverFactory =>

  protected override val traced = Environment.boolean("TRACE_REMOTE_WEB_DRIVER", default = true)
}

/**
 * Use this trait to always see the commands executed by the underlying RemoteWebDriver
 */
trait AlwaysTracedRemoteDriverFactory {
  this: RemoteDriverFactory =>

  protected override val traced = true
}

/**
 * Patience configuration for RemoteDriverFactory
 */
object RemoteDriverFactory extends WaitFor {

  // how long to wait for the RemoteWebDriver connection
  object CreateDriver extends PatienceConfig(60.seconds)

}

/**
 * Default implementation to create RemoteWebDriver
 */
trait RemoteDriverFactory extends DynamicDriverFactory with WaitFor {
  protected def capabilities(): Capabilities

  protected def url(): String

  protected val traced = false

  // default is 2 minutes in HttpClientFactory
  protected val connectionTimeout: FiniteDuration = 2.minutes
  // default is 3 hours in HttpClientFactory, this is too long for this use case
  protected val socketTimeout: FiniteDuration = 2.minutes

  private val createWebDriverRetryCount = 3

  protected def createRealWebDriver(): WebDriver = {
    def tryCreateWebDriver(n: Int = createWebDriverRetryCount): WebDriver = {
      try {
        waitFor(RemoteDriverFactory.CreateDriver) {
          createRealWebDriver2()
        }
      } catch {
        case _: UnreachableBrowserException if n > 1 =>
          tryCreateWebDriver(n - 1)
      }
    }

    tryCreateWebDriver()
  }

  private def createRealWebDriver2(): WebDriver = {
    // we need to override the very long default socketTimeout...
    val factory = new HttpClientFactory(connectionTimeout.toMillis.toInt, socketTimeout.toMillis.toInt)
    val executor = new HttpCommandExecutor(ImmutableMap.of[String, CommandInfo](), new URL(url()),
      new ApacheHttpClient.Factory(factory))
    waitFor(RemoteDriverFactory.CreateDriver) {
      createWebDriver(executor, capabilities())
    }
  }

  protected def createWebDriver(executor: HttpCommandExecutor, capabilities: Capabilities): WebDriver = {
    if (traced) {
      new TracedRemoteWebDriver(executor, capabilities, None.orNull)
    } else {
      new RemoteWebDriver(executor, capabilities, None.orNull)
    }
  }
}
