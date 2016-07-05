/*
 * Copyright 2001-2016 Artima, Inc.
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
package org.pageobject.examples.tour03

import org.pageobject.core.WaitFor
import org.pageobject.core.browser.PageBrowser
import org.pageobject.core.page.DomainPage
import org.pageobject.core.page.PageModule
import org.pageobject.core.page.PageObject
import org.pageobject.scalatest.PageObjectSuite
import org.scalatest.FunSpec

import scala.util.Try

/**
 * It is recommended to create a DomainPage if you are testing more then one page in the same domain.
 */
abstract class Tour03GoogleDomainPage extends PageObject with DomainPage {
  /**
   * For this example the domain is hardcoded. But you can also do something like:
   * val domain = sys.env.getOrElse("DEV_SERVER_PREFIX", "https://www.google.com")
   */
  val domain = "https://www.google.com"
}

/**
 * This is the "Page Object Pattern" object for the google search page.
 *
 * A Page Object should not contain any state.
 */
case class Tour03GoogleSearchPage() extends Tour03GoogleDomainPage {
  val path = "/"

  /**
   * a content object is required, it should contain functions returning elements from the page
   * and functions for actions the user can trigger
   */
  object content extends PageModule {
    // in this example, we only need to access the element with name "q"
    private val q = textField(name("q"))

    def search(value: String): Unit = {
      q.value = value
      submit()
    }

    def searchTerm(): String = q.value
  }

  /**
   * The at() function should contain checks to see if the browser is at this page.
   * You can do this by quering the page title or accessing elements provided by content.
   */
  override def atChecker() = pageTitle == "Google"
}

/**
 * A page object representing a search result page.
 *
 * This page has no path, because of this <code>to()</code> can not be used, only <code>at()</code>.
 * If you want to implement path (not needed for this example):
 * <code>val path = s"/#q=${searchTerm}"</code>
 *
 * @param searchTerm the searched term
 */
case class Tour03GoogleResultPage(searchTerm: String) extends PageObject {

  // the part after "Google" will vary depending on the browser locale
  override def atChecker() = pageTitle.startsWith(s"$searchTerm - Google")
}

/**
 * If you are using WebBrowser inside of your test class (this is not recommended)
 * you also need to use trait DefaultDriverProvider.
 *
 * trait PageBrowser is needed to use <code>to()</code> and <code>at()</code>.
 */
class Tour03 extends FunSpec with PageObjectSuite with WaitFor {
  describe("google.com") {
    it("should change its title based on the term searched") {
      // navigate the browser to the given page and activate it
      val page = to(Tour03GoogleSearchPage())
      page.content.search("Cheese!")

      // activate the new page without navigating to it
      at(Tour03GoogleResultPage("Cheese!"))
    }

    it("long example") {
      // navigate the browser to the given page and activate it
      val firstPage = to(Tour03GoogleSearchPage())
      firstPage.content.search("Cheese!")

      // activate the new page without navigating to it
      val secondPage = at(Tour03GoogleResultPage("Cheese!"))

      // accessing page.content.q will fail because this page is no longer the active one
      assert(Try(firstPage.content.searchTerm()).isFailure)

      // at check for Tour03GoogleSearchPage should fail
      withPatience(PageBrowser.At -> 1.second) {
        assert(Try(at(Tour03GoogleSearchPage())).isFailure)
      }
    }
  }
}
