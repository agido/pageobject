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
package org.pageobject.core.api

import org.pageobject.core.TestSpec
import org.pageobject.core.dsl.BrowserPageDsl
import org.pageobject.core.page.OwnPageReference
import org.pageobject.core.page.PageModule
import org.pageobject.scalatest.JettySuite.JettyPage

class ElementTest extends TestSpec with BrowserPageDsl {

  case class ElementTestPage[E <: Element](tag: String, queryToLocatorRef: (Query, OwnPageReference) => Locator[E]) extends JettyPage {
    val path = s"/$tag.html"

    object content extends PageModule {
      private def queryToLocator(query: Query): Locator[E] = queryToLocatorRef(query, ownPageReference)

      private def byId = queryToLocator(id("id"))

      private def byName = queryToLocator(name("name"))

      private def byClass = queryToLocator(className("class"))

      private def byCss = queryToLocator(cssSelector("""[attr="attr"]"""))

      def test(): Unit = {
        assert(byId.text == s"$tag by id")
        assert(byName.text == s"$tag by name")
        assert(byClass.text == s"$tag by class")
        assert(byCss.text == s"$tag by css")
      }
    }

    override def atChecker() = pageTitle == s"$tag Element Test"
  }

  def test[E <: Element](tag: String, queryToLocatorRef: (Query, OwnPageReference) => Locator[E]): Unit = {
    it("should be able to use queryToLocator") {
      val page = to(ElementTestPage(tag, queryToLocatorRef))
      page.content.test()
    }
    it("should be able to use UntypedLocator") {
      val untypedPage = to(ElementTestPage(tag, (query, own) => $(query)(own)))
      untypedPage.content.test()
    }
  }

  describe("div") {
    test("div", (query, own) => div(query)(own))
  }

  describe("span") {
    test("span", (query, own) => span(query)(own))
  }

  describe("a") {
    test("a", (query, own) => a(query)(own))
  }

  describe("button") {
    test("button", (query, own) => a(query)(own))
  }
}
