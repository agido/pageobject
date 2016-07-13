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
package org.pageobject.examples.wikipedia

import org.pageobject.examples.ExamplePageObjectSpec

// TODO this is not working in scala 2.10
// import org.pageobject.scalatest.tags.InternetTest
// @InternetTest
class WikipediaTest extends ExamplePageObjectSpec {
  private val term = "Selenium (software)"

  describe("wikipedia homepage") {
    it("should be possible to search for Selenium") {
      Given("the wikipedia homepage")
      val page = to(WikipediaHomepage())

      When("search language is english")
      page.search.language = "en"

      And(s"we search for '$term'")
      page.search(term)

      Then(s"we are at the $term page")
      at(WikipediaEnPage(term))
    }
  }

  describe("wikipedia article") {
    it("should be possible to focus the search field by pressing tab") {
      val page = to(WikipediaEnPage(term))

      // search field initially has no focus
      assert(!page.search.searchHasFocus)

      // pressing tab should focus the search
      page.keyboard.pressTab()
      assert(page.search.searchHasFocus)

      // pressing tab again should remove focus
      page.keyboard.pressTab()
      assert(!page.search.searchHasFocus)

      // but using shift tab the focus should be back
      page.keyboard.pressShiftTab()
      assert(page.search.searchHasFocus)
    }
  }
}
