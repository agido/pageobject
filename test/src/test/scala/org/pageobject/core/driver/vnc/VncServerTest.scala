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

import org.easymock.EasyMock
import org.easymock.EasyMock.expect
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.UnreachableBrowserException
import org.pageobject.core.browser.PageHolder
import org.pageobject.core.driver.RunWithDrivers
import org.pageobject.core.driver.TestVncDriverFactoryList
import org.pageobject.core.page.PageModule
import org.pageobject.core.page.PageObject
import org.pageobject.core.page.UnexpectedPagesFactory
import org.pageobject.core.page.UrlPage
import org.pageobject.scalatest.InstanceOf
import org.pageobject.scalatest.PageObjectSuite
import org.pageobject.scalatest.PageObjectSuiteRunner
import org.pageobject.scalatest.RunnerResult
import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen
import org.scalatest.OptionValues
import org.scalatest.Suite
import org.scalatest.easymock.EasyMockSugar
import org.scalatest.events.Event
import org.scalatest.events.SuiteAborted
import org.scalatest.events.SuiteCompleted
import org.scalatest.events.SuiteStarting
import org.scalatest.events.TestFailed
import org.scalatest.events.TestStarting
import org.scalatest.events.TestSucceeded
import org.scalatest.exceptions.TestFailedException

import scala.collection.JavaConverters.seqAsJavaListConverter

object VncServerTest {

  @RunWithDrivers(classOf[TestVncDriverFactoryList])
  class TestSuiteDoingNothing extends FunSpec with PageObjectSuite {
    it("should work") {}
  }

  case class TestSuiteGetTextPage() extends PageObject with UrlPage {
    val url = "http://some/url"

    object content extends PageModule {
      private val locator = $(id("id"))

      def text: String = locator.text
    }

    override def atChecker(): Boolean = true
  }

  @RunWithDrivers(classOf[TestVncDriverFactoryList])
  class TestSuiteGetText extends FunSpec with PageObjectSuite {
    it("should work") {
      val page = to(TestSuiteGetTextPage())
      assert(page.content.text == "some text")
    }
  }

  @RunWithDrivers(classOf[TestVncDriverFactoryList])
  class TestSuiteFailing extends FunSpec with PageObjectSuite {
    it("should work") {
      sys.error("test failed")
    }
  }

  @RunWithDrivers(classOf[TestVncDriverFactoryList])
  class UnexpectedPagesTest extends FunSpec with PageObjectSuite {
    it("should work") {
      val unexpectedPages = PageHolder.withPageHolder(this) {
        UnexpectedPagesFactory.createUnexpectedPages()
      }
      assert(unexpectedPages.cancelTestPages.isEmpty)
      assert(unexpectedPages.failTestPages.isEmpty)
      assert(unexpectedPages.waitPages.isEmpty)
    }
  }

}

class VncServerTest extends FunSpec with EasyMockSugar with GivenWhenThen with InstanceOf with OptionValues {
  private def check(clazz: Class[_ <: Suite with PageObjectSuite],
                    mockProvider: () => (RemoteWebDriver, Seq[_ <: AnyRef]),
                    succeed: Boolean,
                    classes: Seq[Class[_ <: Event]]
                   ): RunnerResult = {
    val (webDriver, otherMocks) = mockProvider()
    val mocks = Seq(webDriver) ++ otherMocks

    val result = whenExecuting(mocks: _*) {
      PageObjectSuiteRunner(clazz, webDriver)
    }

    checkResult(result, succeed, classes)
    result
  }

  private def checkResult(result: RunnerResult, succeed: Boolean, classes: Seq[Class[_ <: Event]]): Unit = {
    if (!classes.contains(classOf[TestFailed])) {
      Then("should there be no unexpected TestFailed exception")
      result.events.firstInstanceOf(classOf[TestFailed])
        .flatMap(_.throwable)
        .foreach(th => throw th)
    }
    if (!classes.contains(classOf[SuiteAborted])) {
      Then("should there be no unexpected SuiteAborted exception")
      result.events.firstInstanceOf(classOf[SuiteAborted])
        .flatMap(_.throwable)
        .foreach(th => throw th)
    }

    if (succeed) {
      Then("should the status be succeed")
      assert(result.status.succeeds())
    } else {
      Then("should the status be not succeed")
      assert(!result.status.succeeds())
    }

    And("events should be correct")
    assertInstanceOf(result.events, classes: _*)
  }

  private def checkTestSucceeded(clazz: Class[_ <: Suite with PageObjectSuite],
                                 mockProvider: () => (RemoteWebDriver, Seq[_ <: AnyRef])
                                ): Unit = {
    check(clazz, mockProvider, succeed = true, Seq(
      classOf[SuiteStarting],
      classOf[TestStarting],
      classOf[TestSucceeded],
      classOf[SuiteCompleted]
    ))
  }

  private def checkTestFail(clazz: Class[_ <: Suite with PageObjectSuite],
                            mockProvider: () => (RemoteWebDriver, Seq[_ <: AnyRef])
                           ): Throwable = {
    val result = check(clazz, mockProvider, succeed = false, Seq(
      classOf[SuiteStarting],
      classOf[TestStarting],
      classOf[TestFailed],
      classOf[SuiteCompleted]
    ))
    result.events.firstInstanceOf(classOf[TestFailed]).value.throwable.get
  }

  private def checkSuiteAbortedAfterTest(clazz: Class[_ <: Suite with PageObjectSuite],
                                         mockProvider: () => (RemoteWebDriver, Seq[_ <: AnyRef])
                                        ): Throwable = {
    val result = check(clazz, mockProvider, succeed = false, Seq(
      classOf[SuiteStarting],
      classOf[TestStarting],
      classOf[TestSucceeded],
      classOf[SuiteAborted],
      classOf[SuiteCompleted]
    ))
    result.events.firstInstanceOf(classOf[SuiteAborted]).value.throwable.get
  }

  private def checkSuiteAbortedNoTest(result: RunnerResult): Throwable = {
    checkResult(result, succeed = false, Seq(
      classOf[SuiteStarting],
      classOf[SuiteAborted],
      classOf[SuiteCompleted]
    ))

    result.events.firstInstanceOf(classOf[SuiteAborted]).value.throwable.get
  }

  def webElementMock(): WebElement = {
    val webElement = mock[WebElement]
    expecting {
      expect(webElement.getText).andReturn("some text")
    }
    webElement
  }

  def unreachableBrowserException: UnreachableBrowserException = {
    new UnreachableBrowserException("Error communicating with the remote browser. It may have died.",
      new java.net.SocketTimeoutException("Read timed out"))
  }

  it("should dectect unexpected pages") {
    checkTestSucceeded(classOf[VncServerTest.UnexpectedPagesTest], () => {
      val webDriver = strictMock[RemoteWebDriver]
      expecting {
        webDriver.close()
      }
      (webDriver, Nil)
    })
  }

  it("should handle a successful test") {
    checkTestSucceeded(classOf[VncServerTest.TestSuiteDoingNothing], () => {
      val webDriver = strictMock[RemoteWebDriver]
      expecting {
        webDriver.close()
      }
      (webDriver, Nil)
    })
  }

  it("should handle delayed get by id") {
    checkTestSucceeded(classOf[VncServerTest.TestSuiteGetText], () => {
      val webElement = webElementMock()
      val webDriver = strictMock[RemoteWebDriver]
      expecting {
        webDriver.get("http://some/url")
        expect(webDriver.findElements(EasyMock.anyObject()))
          .andReturn(Seq().asJava)
          .times(10) // scalastyle:ignore magic.number
        expect(webDriver.findElements(By.id("id")))
          .andReturn(Seq(webElement).asJava)
          .anyTimes()
        webDriver.close()
      }
      (webDriver, Seq(webElement))
    })
  }

  it("should handle get by id") {
    checkTestSucceeded(classOf[VncServerTest.TestSuiteGetText], () => {
      val webElement = webElementMock()
      val webDriver = strictMock[RemoteWebDriver]
      expecting {
        webDriver.get("http://some/url")
        expect(webDriver.findElements(By.id("id")))
          .andReturn(Seq(webElement).asJava)
        webDriver.close()
      }
      (webDriver, Seq(webElement))
    })
  }

  it("should handle unexpected connection timeouts") {
    val exception = checkTestFail(classOf[VncServerTest.TestSuiteGetText], () => {
      val webDriver = strictMock[RemoteWebDriver]
      expecting {
        webDriver.get("http://some/url")
        expect(webDriver.findElements(EasyMock.anyObject()))
          .andReturn(Seq().asJava)
          .times(10) // scalastyle:ignore magic.number
        expect(webDriver.findElements(By.id("id")))
          .andThrow(unreachableBrowserException)
        webDriver.close()
      }
      (webDriver, Nil)
    })
    assert(exception.isInstanceOf[UnreachableBrowserException])
  }

  it("should handle unexpected connection timeouts on get") {
    val exception = checkTestFail(classOf[VncServerTest.TestSuiteGetText], () => {
      val webDriver = strictMock[RemoteWebDriver]
      expecting {
        expect(webDriver.get("http://some/url"))
          .andThrow(unreachableBrowserException)
      }
      (webDriver, Nil)
    })
    assert(exception.isInstanceOf[UnreachableBrowserException])
  }

  it("should handle unexpected connection timeouts on close") {
    checkTestSucceeded(classOf[VncServerTest.TestSuiteDoingNothing], () => {
      val webDriver = strictMock[RemoteWebDriver]
      expecting {
        expect(webDriver.close()).andThrow(unreachableBrowserException)
      }
      (webDriver, Nil)
    })
  }

  it("should handle test failure") {
    val exception = checkTestFail(classOf[VncServerTest.TestSuiteFailing], () => {
      val webDriver = strictMock[RemoteWebDriver]
      expecting {
        webDriver.close()
      }
      (webDriver, Nil)
    })
    assert(exception.getMessage == "test failed")
  }

  it("should handle exception while creating WebDriver") {
    val exceptionMessage = "P1j94Gjd7PMdPMCs0Sy1cFGqJN91draC"

    val webDriverFactory: () => RemoteWebDriver = () => {
      And("simulating connection refused")
      throw new UnreachableBrowserException(exceptionMessage)
    }

    When("Test Suite is running")
    val result = PageObjectSuiteRunner(classOf[VncServerTest.TestSuiteDoingNothing], webDriverFactory)
    val exception = checkSuiteAbortedNoTest(result)
    assert(exception.getMessage.startsWith(exceptionMessage))
  }

  it("should handle RuntimeException while creating WebDriver") {
    val exceptionMessage = "CmnMjdtVQaFvgRhrdTfuMfD5308DoHeq"

    val webDriverFactory: () => RemoteWebDriver = () => {
      And("simulating RuntimeException")
      throw new RuntimeException(exceptionMessage)
    }

    When("Test Suite is running")
    val result = PageObjectSuiteRunner(classOf[VncServerTest.TestSuiteDoingNothing], webDriverFactory)
    val exception = checkSuiteAbortedNoTest(result)
    assert(exception.getMessage.startsWith(exceptionMessage))
  }
}
