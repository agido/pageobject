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

import org.pageobject.core.TestHelper
import org.pageobject.core.driver.DefaultDriverFactoryList
import org.pageobject.core.driver.DriverFactories
import org.pageobject.core.driver.DriverFactory
import org.pageobject.core.driver.DriverFactoryHolder
import org.pageobject.core.driver.RunWithDrivers
import org.pageobject.core.page.UnexpectedPagesFactory
import org.scalatest.Args
import org.scalatest.DoNotDiscover
import org.scalatest.PageObjectHelper
import org.scalatest.ParallelTestExecution
import org.scalatest.Status
import org.scalatest.Suite
import org.scalatest.Suites
import org.scalatest.tools.AnnotationHelper

/**
 * Helper Object to get the corresponding DriverFactories
 */
object DriverLaunchWrapper {
  def getDriverFactories(clazz: Class[_]): DriverFactories = {
    sys.env.get("RUN_WITH_DRIVERS")
      .map(Class.forName(_).asInstanceOf[Class[DriverFactories]])
      .orElse(AnnotationHelper.find(classOf[RunWithDrivers], clazz).map(_.value()))
      .orElse(sys.env.get("IGNORE_DEFAULT_DRIVER") match {
        case None | Some("0") | Some("false") => Some(classOf[DefaultDriverFactoryList])
        case Some(_) => None
      })
      .getOrElse(TestHelper.notAllowed("Missing RUN_WITH_DRIVERS environment variable!"))
      .newInstance()
  }
}

/**
 * This class will wrap the "real" test suite.
 *
 * A list of driver factories is queried from <code>&#064;RunWithDrivers</code>
 *
 * For each driver factory an instance of the "real" test suite is created.
 **/
@DoNotDiscover
class DriverLaunchWrapper(clazz: Class[_ <: DriverLauncher with Suite])
  extends Suites with ParallelTestExecution with ConfigureableParallelTestLimit {

  private val currentMock = DriverFactory.currentMock

  private val runWith = DriverLaunchWrapper.getDriverFactories(clazz)

  private def createBrowserSuiteInstance(driverFactory: DriverFactory) = {
    DriverFactoryHolder.withValue(Some(driverFactory)) {
      DriverFactory.withWebDriverMock(currentMock) {
        clazz.getConstructor().newInstance()
      }
    }
  }

  override val nestedSuites = UnexpectedPagesFactory.withMaybeUnexpectedPages(runWith) {
    runWith.drivers().map(config => createBrowserSuiteInstance(config)).toIndexedSeq
  }

  override def suiteName = PageObjectHelper.suiteName(clazz)

  override def suiteId = clazz.getName

  override def run(testName: Option[String], args: Args): Status = {
    UnexpectedPagesFactory.withMaybeUnexpectedPages(runWith) {
      super.run(testName, args)
    }
  }
}
