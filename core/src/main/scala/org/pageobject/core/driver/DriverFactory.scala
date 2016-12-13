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

import java.util.concurrent.atomic.AtomicInteger

import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.pageobject.core.SeleniumException
import org.pageobject.core.TestHelper
import org.pageobject.core.WaitFor.PatienceConfig
import org.pageobject.core.tools.DynamicOptionVariable
import org.pageobject.core.tools.Limit.Limit
import org.pageobject.core.tools.LogContext
import org.pageobject.core.tools.Logging
import org.pageobject.core.tools.Perf

import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.util.control.NonFatal

private[pageobject] object DriverFactory {

  private object mock extends DynamicOptionVariable[() => WebDriver]()

  def currentMock: Option[() => WebDriver] = mock.option

  def withWebDriverMock[S](webDriverFactory: () => WebDriver)(thunk: => S): S = {
    withWebDriverMock(Some(webDriverFactory))(thunk)
  }

  def withWebDriverMock[S](webDriverFactory: Option[() => WebDriver])(thunk: => S): S = {
    mock.withValue(webDriverFactory) {
      thunk
    }
  }
}

/**
 * This trait is used to configure a test run and create a WebDriver used by the test.
 */
trait DriverFactory extends IDriverFactory {
  def limit: Limit

  def compatible: Boolean = true

  def takeScreenshot(testName: String, webDriver: WebDriver with TakesScreenshot): Unit = {}

  def webDriver: WebDriver

  def runTests[T](fn: => T): T = fn

  private def takeScreenshot(testName: String): Unit = {
    if (webDriver.isInstanceOf[TakesScreenshot]) {
      takeScreenshot(testName, webDriver.asInstanceOf[WebDriver with TakesScreenshot])
    }
  }

  def runTest[T](testName: String, fn: => T): T = {
    try {
      val result = fn
      if (TestHelper.isFailedResult(result)) {
        takeScreenshot(testName)
      }
      result
    } catch {
      case NonFatal(e) =>
        takeScreenshot(testName)
        throw e
    }
  }

  def timeouts: Set[PatienceConfig] = Set()

  protected def createWebDriver(): WebDriver = DriverFactory.currentMock match {
    case Some(create) =>
      create()
    case None =>
      createRealWebDriver()
  }

  protected def createRealWebDriver(): WebDriver
}

private object LoggingDriverFactory extends Logging

trait LoggingDriverFactory extends DriverFactory {
  private def space(string: String): String = {
    if (string.contains(" ")) {
      s"'$string'"
    } else {
      string
    }
  }

  override def runTest[T](testName: String, fn: => T): T = {
    val suiteName = LogContext(LogContext.suiteName)
    val testName = LogContext(LogContext.testName)
    val browser = LogContext(LogContext.browser)
    val name = s"${space(suiteName)} ${space(testName)} on ${space(browser)}"
    LoggingDriverFactory.debug(s"preparing $name")
    Perf((ms: Long, t: Try[T]) => t match {
      case Success(_) =>
        LoggingDriverFactory.debug(s"$name has taken ${ms}ms\n")
      case Failure(exception) =>
        LoggingDriverFactory.error(s"$name has failed after ${ms}ms\n")
        TestHelper.failedResult(exception)
    }) {
      super.runTest(testName, {
        LoggingDriverFactory.info(s"running $name")
        try {
          val ret = LogContext(Map(LogContext.activePage -> "")) {
            fn
          }
          if (TestHelper.isFailedResult(ret)) {
            LoggingDriverFactory.error(s"failed running $name")
          } else {
            LoggingDriverFactory.info(s"finished $name")
          }
          ret
        } catch {
          case NonFatal(th) =>
            LoggingDriverFactory.error(s"failed running $name", th)
            throw th
        }
      })
    }
  }
}

private object TestNumbering extends AtomicInteger

trait ThreadNameNumberingDriverFactory extends DriverFactory with Logging {
  override def runTest[T](testName: String, fn: => T): T = {
    val thread = Thread.currentThread()
    val oldName = thread.getName
    val id = TestNumbering.incrementAndGet()
    thread.setName(f"#$id%04d")
    try {
      super.runTest(testName, fn)
    } finally {
      thread.setName(oldName)
    }
  }
}

/**
 * When using this trait the browser started by this driver will be switched into fullscreen mode.
 */
trait Fullscreen extends DriverFactory {
  abstract override def createRealWebDriver(): WebDriver = {
    val driver: WebDriver = super.createRealWebDriver()

    def window = driver.manage().window()

    window.fullscreen()
    driver
  }
}

/**
 * When using this trait the browser started by this driver will be maximized.
 */
trait Maximized extends DriverFactory {
  abstract override def createRealWebDriver(): WebDriver = {
    val driver: WebDriver = super.createRealWebDriver()

    def window = driver.manage().window()

    window.maximize()
    driver
  }
}

/**
 * When using this trait the browser window will be moved and sized to a fixed location.
 */
trait FixedLocation extends DriverFactory {
  val position = Some(new Point(0, 0))
  val size = Some(new Dimension(1920, 1080)) // scalastyle:ignore magic.number

  abstract override def createRealWebDriver(): WebDriver = {
    val driver: WebDriver = super.createRealWebDriver()

    def window = driver.manage().window()

    position.foreach(window.setPosition(_))
    size.foreach(window.setSize(_))
    driver
  }
}

/**
 * A default implementation for DriverFactory.webDriver,
 * using a DynamicOptionVariable to store the active webDriver on the stack.
 * A new webDriver will be created for every test.
 */
trait DynamicDriverFactory extends DriverFactory {

  private object webDriverHolder extends DynamicOptionVariable[WebDriver]()

  def webDriver: WebDriver = webDriverHolder.value

  override def runTest[T](testName: String, fn: => T): T = {
    webDriverHolder.withValue(Some(createWebDriver())) {
      try {
        val result = super.runTest(testName, fn)
        try {
          webDriver.quit()
        } catch {
          case NonFatal(ex) => // ignore
        }
        result
      } catch {
        case NonFatal(nf) if !SeleniumException(nf) =>
          webDriver.quit()
          throw nf
      }
    }
  }
}
