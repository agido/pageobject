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
package org.pageobject.examples.tour

import org.pageobject.core.page.PageModule
import org.pageobject.core.page.PageObject
import org.pageobject.core.page.UrlPage
import org.pageobject.scalatest.PageObjectSuite
import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen

case class GoogleSearchHomePage() extends PageObject with UrlPage {
  val url = "https://www.google.com"

  object content extends PageModule {
    private def q = textField(name("q"))

    def search(searchTerm: String): Unit = {
      q.value = searchTerm
      submit()
    }
  }

  override def atChecker() = pageTitle == "Google"
}

case class GoogleSearchResultPage(searchTerm: String) extends PageObject {
  // the part after "Google" will vary depending on the browser locale
  override def atChecker() = pageTitle.startsWith(s"$searchTerm - Google")
}

// you can also use any other testing style provided by ScalaTest
class ExampleSpec extends FunSpec with PageObjectSuite with GivenWhenThen {
  describe("Google Search") {
    it("should change its title based on the term searched") {
      Given("The Google Search Homepage")
      val page = to(GoogleSearchHomePage())

      When("Searching for “Cheese!”")
      page.content.search("Cheese!")

      Then("We are at the Google Search Result Page")
      at(GoogleSearchResultPage("Cheese!"))
    }
  }
}
