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
package org.pageobject.core

import java.util.concurrent.atomic.AtomicInteger

import org.pageobject.core.browser.PageBrowser
import org.pageobject.core.browser.PageHolder
import org.pageobject.core.driver.DefaultDriverProvider
import org.pageobject.core.page.ActivePage
import org.pageobject.core.page.AtChecker
import org.pageobject.core.page.EmptyUnexpectedPagesFactory
import org.pageobject.core.page.PageObject
import org.pageobject.core.page.UnexpectedPagesFactory
import org.pageobject.core.page.UrlPage
import org.pageobject.scalatest.ConfigureableParallelTestLimit
import org.pageobject.scalatest.DriverLauncher
import org.scalatest.BeforeAndAfterEach
import org.scalatest.FunSpec

class ActivePageTestSingleThreaded extends ActivePageTest

class ActivePageTestMultiThreaded extends ActivePageTest with ConfigureableParallelTestLimit

trait ActivePageTest extends FunSpec with PageBrowser with DriverLauncher with DefaultDriverProvider with BeforeAndAfterEach {
  val unusedLocalPort1 = 65534
  val unusedLocalPort2 = 65533

  case class TestPage(port: Int = unusedLocalPort1) extends PageObject with UrlPage with ActivePage {
    override def url = s"http://localhost:$port/"

    override def atChecker() = true

    val activated = new AtomicInteger()
    val deactivated = new AtomicInteger()

    override def onActivated(pageHolder: PageHolder): Unit = {
      activated.incrementAndGet()
    }

    override def onDeactivated(pageHolder: PageHolder): Unit = {
      deactivated.incrementAndGet()
    }
  }

  private val activated = new AtomicInteger()
  private val deactivated = new AtomicInteger()

  protected override def onActivated(page: AtChecker): Unit = {
    activated.incrementAndGet()
    super.onActivated(page)
  }

  protected override def onDeactivated(page: AtChecker): Unit = {
    deactivated.incrementAndGet()
    super.onDeactivated(page)
  }

  protected override def beforeEach() = {
    activated.set(0)
    deactivated.set(0)
  }

  private def assertActivated(a: Int, d: Int): Unit = {
    assert(activated.get() == a, "wrong global activated count")
    assert(deactivated.get() == d, "wrong global deactivated count")
  }

  private def assertActivated(page: TestPage, a: Int, d: Int): Unit = {
    assert(page.activated.get() == a, "wrong page activated count")
    assert(page.deactivated.get() == d, "wrong page deactivated count")
  }

  it("should not count via") {
    UnexpectedPagesFactory.withUnexpectedPages(EmptyUnexpectedPagesFactory()) {
      val page = via(TestPage())
      assertActivated(0, 0)
      assertActivated(page, 0, 0)
    }
  }

  it("should count at") {
    UnexpectedPagesFactory.withUnexpectedPages(EmptyUnexpectedPagesFactory()) {
      val page = at(TestPage())
      assertActivated(1, 0)
      assertActivated(page, 1, 0)
    }
  }

  it("should count to") {
    UnexpectedPagesFactory.withUnexpectedPages(EmptyUnexpectedPagesFactory()) {
      val page = to(TestPage())
      assertActivated(1, 0)
      assertActivated(page, 1, 0)
    }
  }

  it("should count invalidateActivePage") {
    UnexpectedPagesFactory.withUnexpectedPages(EmptyUnexpectedPagesFactory()) {
      val page = at(TestPage())
      invalidateActivePage()
      assertActivated(1, 1)
      assertActivated(page, 1, 1)
    }
  }

  it("should count at two times") {
    UnexpectedPagesFactory.withUnexpectedPages(EmptyUnexpectedPagesFactory()) {
      val page1 = at(TestPage())
      val page2 = at(TestPage())
      assertActivated(2, 1)
      assertActivated(page1, 1, 1)
      assertActivated(page2, 1, 0)
    }
  }

  it("should count at two times called on same page") {
    UnexpectedPagesFactory.withUnexpectedPages(EmptyUnexpectedPagesFactory()) {
      val page = at(TestPage())
      at(page)
      assertActivated(2, 1)
      assertActivated(page, 2, 1)
    }
  }
}
