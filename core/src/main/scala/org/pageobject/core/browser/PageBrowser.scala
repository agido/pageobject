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
package org.pageobject.core.browser

import org.pageobject.core.TestHelper
import org.pageobject.core.WaitFor
import org.pageobject.core.page.AtChecker
import org.pageobject.core.page.UnexpectedPagesFactory
import org.pageobject.core.page.UrlPage
import org.pageobject.core.tools.Logging

/**
 * Patience configuration for PageBrowser
 */
object PageBrowser extends WaitFor {

  // timeout how long to wait for the expected page
  object At extends PatienceConfig(30.seconds)

}

/**
 * This trait provides page object navigation support.
 *
 * It should be used by your tests, not by page objects.
 */
trait PageBrowser extends WaitFor with PageHolder with Logging {
  /**
   * Starts navigation to the given page.
   *
   * @param page to page to navigate to
   */
  def via[P <: UrlPage](page: => P): P = {
    val pageDeferred = defer(page)
    clearActivePage()
    webDriver.get(pageDeferred.url)
    pageDeferred
  }

  /**
   * navigates and activates the given page.
   *
   * @param page to page to navigate to
   *
   * @tparam P the type of the page object
   *
   * @return the given page
   */
  def to[P <: UrlPage with AtChecker](page: => P): P = {
    val pageDeferred = defer(page)
    via(pageDeferred)
    at(pageDeferred)
  }

  /**
   * Asserts that the browser is at the given page
   * and stores the page as the active one.
   *
   * @param page the page to activate.
   *
   * @tparam P the type of the page to activate.
   *
   * @return the activated page.
   */
  def at[P <: AtChecker](page: => P): P = {
    val pageDeferred = defer(page)
    clearActivePage()

    val unexpectedPages = defer(UnexpectedPagesFactory.createUnexpectedPages())
    waitFor(PageBrowser.At) {
      unexpectedPages.waitPages.find(isAt(_)).foreach(unexpectedPage => {
        info(s"browser is at unexpected wait page $unexpectedPage!")
        throw new RuntimeException(s"browser is at unexpected wait page $unexpectedPage!")
      })
      unexpectedPages.cancelTestPages.find(isAt(_)).foreach(unexpectedPage => {
        error(s"browser is at unexpected wait page $unexpectedPage!")
        TestHelper.cancelTest(s"browser is at unexpected page $unexpectedPage, test canceled!")
      })
      unexpectedPages.failTestPages.find(isAt(_)).foreach(unexpectedPage => {
        error(s"browser is at unexpected wait page $unexpectedPage!")
        TestHelper.failTest(s"browser is at unexpected page $unexpectedPage, test failed!")
      })
      assert(isAt(pageDeferred), s"Browser is not at expected page $pageDeferred!")
    }
    info(s"browser is now at page $pageDeferred!")
    activePage = pageDeferred
    pageDeferred
  }

  /**
   * Check if the given page is the active one.
   *
   * @return true when the browser is at the given page
   */
  def isAt(page: => AtChecker): Boolean = {
    val pageDeferred = defer(page)
    debug(s"running at checker for page $pageDeferred")
    val at = withActivePage(pageDeferred) {
      pageDeferred.atChecker()
    }
    if (at) {
      debug(s"browser is at page $pageDeferred!")
    }
    at
  }

  private def defer[T](page: => T): T = {
    PageHolder.withPageHolder(this) {
      page
    }
  }
}
