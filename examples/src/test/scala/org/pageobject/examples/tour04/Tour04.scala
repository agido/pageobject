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
package org.pageobject.examples.tour04

import org.pageobject.core.page.DomainPage
import org.pageobject.core.page.PageModule
import org.pageobject.core.page.PageObject
import org.pageobject.core.page.ParentPageReference
import org.pageobject.scalatest.PageObjectSuite
import org.scalatest.FunSpec

/**
 * It is recommended to create a DomainPage if you are testing more then one page in the same domain.
 */
trait Tour04GoogleDomainPage extends PageObject with DomainPage {
  /**
   * For this example the domain is hardcoded. But you can also do something like:
   * val domain = sys.env.getOrElse("DEV_SERVER_PREFIX", "https://www.google.com")
   */
  val domain = "https://www.google.com"

  object header extends Tour04GoogleAppsMenu

}

class Tour04GoogleSearchContent(implicit parent: ParentPageReference) extends PageModule {
  // in this example, we only need to access the element with name "q"
  private val q = textField(name("q"))

  def search(value: String): Unit = {
    q.value = value
    submit()
  }
}

class Tour04GoogleAppsMenu(implicit parent: ParentPageReference) extends PageModule {
  private def openMenuButton = div(id("gbwa"))

  private def menu = div(cssSelector("#gbwa > [role=\"region\"]"))

  def openGoogleAppsMenu(): Unit = {
    click on openMenuButton
  }

  def isGoogleAppsMenuOpen: Boolean = {
    menu.isDisplayed
  }
}

/**
 * This is the "Page Object Pattern" object for the google search page.
 *
 * A Page Object should not contain any state.
 */
case class Tour04GoogleSearchPage() extends Tour04GoogleDomainPage {
  val path = "/"

  /**
   * a content object is required, it should contain functions returning elements from the page
   * and functions for actions the user can trigger
   */
  object content extends Tour04GoogleSearchContent

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
 * This page need no content, accidentally accessing content will throw an exception.
 *
 * @param searchTerm the searched term
 */
case class Tour04GoogleResultPage(searchTerm: String) extends PageObject {

  // the part after "Google" will vary depending on the browser locale
  override def atChecker() = pageTitle.startsWith(s"$searchTerm - Google")
}

/**
 * If you are using WebBrowser inside of your test class (this is not recommended)
 * you also need to use trait DefaultDriverProvider.
 *
 * trait PageBrowser is needed to use <code>to()</code> and <code>at()</code>.
 */
class Tour04 extends FunSpec with PageObjectSuite {
  describe("google.com") {
    it("should change its title based on the term searched") {
      // navigate the browser to the given page and activate it
      val page = to(Tour04GoogleSearchPage())
      page.content.search("Cheese!")

      // activate the new page without navigating to it
      at(Tour04GoogleResultPage("Cheese!"))
    }

    it("should be possible to open google apps menu") {
      val page = to(Tour04GoogleSearchPage())
      assert(!page.header.isGoogleAppsMenuOpen)
      page.header.openGoogleAppsMenu()
      waitFor(1.second) {
        assert(page.header.isGoogleAppsMenuOpen)
      }
    }
  }
}
