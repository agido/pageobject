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

import org.pageobject.core.driver.DriverFactoryHolder
import org.scalatest.Args
import org.scalatest.PageObjectHelper
import org.scalatest.Status
import org.scalatest.Suite
import org.scalatest.SuiteMixin
import org.scalatest.WrapWith

import scala.util.Failure
import scala.util.Success
import scala.util.Try

/**
 * Use this trait if you want to test against different browsers
 *
 * When <code>runTest</code> and <code>runTests</code> are called in this test,
 * this calls are delegated to the <code>DriverFactory</code>
 * to create the driver instance if needed.
 */
@WrapWith(classOf[DriverLaunchWrapper])
trait DriverLauncher extends SuiteMixin {
  this: Suite =>

  private val driverFactory = DriverFactoryHolder.value.get

  abstract override val suiteName = driverFactory.name

  abstract override val suiteId = s"${super.suiteId}$$${driverFactory.name}"

  abstract override protected def runTests(testName: Option[String], args: Args): Status = {
    DriverFactoryHolder.withValue(Some(driverFactory)) {
      Try {
        driverFactory.runTests(super.runTests(testName, args))
      } match {
        case Success(result) => result
        case Failure(ex) => PageObjectHelper.failedStatus(ex)
      }
    }
  }

  abstract override protected def runTest(testName: String, args: Args): Status = {
    DriverFactoryHolder.withValue(Some(driverFactory)) {
      Try {
        driverFactory.runTest(testName, super.runTest(testName, args))
      } match {
        case Success(result) => result
        case Failure(ex) => PageObjectHelper.failedStatus(ex)
      }
    }
  }
}
