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

import org.openqa.selenium.WebDriver
import org.pageobject.core.driver.DriverFactory
import org.scalatest.Args
import org.scalatest.Reporter
import org.scalatest.Status
import org.scalatest.Suite
import org.scalatest.events.Event

case class RunnerResult(status: Status, events: collection.immutable.Seq[Event])

object PageObjectSuiteRunner {
  def apply(clazz: Class[_ <: DriverLauncher with Suite], webDriver: WebDriver): RunnerResult = {
    apply(clazz, () => webDriver)
  }

  def apply(clazz: Class[_ <: DriverLauncher with Suite], webDriverFactory: () => WebDriver): RunnerResult = {
    val events = collection.mutable.ListBuffer[Event]()
    val suite = DriverFactory.withWebDriverMock(webDriverFactory) {
      new DriverLaunchWrapper(clazz)
    }
    val status = suite.run(None, Args(reporter = new Reporter {
      override def apply(event: Event): Unit = events.synchronized {
        events += event
      }
    }))
    status.waitUntilCompleted()
    RunnerResult(status, events.toList)
  }
}
