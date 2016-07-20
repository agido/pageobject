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

import org.openqa.selenium.StaleElementReferenceException
import org.pageobject.core.JettySuite.JettyPage
import org.pageobject.core.TestSpec
import org.pageobject.core.dsl.BrowserPageDsl
import org.pageobject.core.page.PageModule

class StaleElementReferenceTest extends TestSpec with BrowserPageDsl {

  case class StaleElementReferencePage() extends JettyPage {
    val path = "/index.html"

    object content extends PageModule {
      val test1 = $(id("test1"))
      val test2 = $(id("test2"))
      val div = $(tagName("div"))

      def update(str: String): Unit = {
        executeScript(s"""document.body.innerHTML = '<div id="test1">$str</div>'""")
      }

      def update(str1: String, str2: String): Unit = {
        executeScript(s"""document.body.innerHTML = '<div id="test1">$str1</div><div id="test2">$str2</div>'""")
      }
    }

    override def atChecker() = pageTitle == "Test Title"
  }

  it("should be able to read inital Element text") {
    val page = to(StaleElementReferencePage())
    page.content.update("a")
    val element: UntypedElement = page.content.test1
    assert(element.underlying.getText == "a")
  }

  it("should be able to detect a StaleElementReferenceException") {
    val page = to(StaleElementReferencePage())
    page.content.update("b")
    val element: UntypedElement = page.content.test1
    page.content.update("b")
    intercept[StaleElementReferenceException] {
      element.underlying.getText
    }
  }

  it("should be able to fix a StaleElementReferenceException for locator") {
    val page = to(StaleElementReferencePage())
    page.content.update("c1")
    val element: UntypedLocator = page.content.test1
    assert(element.text == "c1")
    page.content.update("c2")
    assert(element.text == "c2")
  }

  it("should be able to fix a StaleElementReferenceException for element") {
    val page = to(StaleElementReferencePage())
    page.content.update("d1")
    val element: UntypedElement = page.content.test1
    assert(element.text == "d1")
    page.content.update("d2")
    assert(element.text == "d2")
  }

  it("should be able to fix a StaleElementReferenceException for elements") {
    val page = to(StaleElementReferencePage())
    page.content.update("e1")
    val elements: Seq[UntypedElement] = page.content.test1.elements
    assert(elements.map(_.text).mkString == "e1")
    page.content.update("e2")
    assert(elements.map(_.text).mkString == "e2")
  }

  it("should be able to fix a StaleElementReferenceException for multiple elements") {
    val page = to(StaleElementReferencePage())
    page.content.update("f1", "f2")
    val elements1: Seq[UntypedElement] = page.content.test1.elements
    val elements2: Seq[UntypedElement] = page.content.test2.elements
    assert(elements1.map(_.text).mkString == "f1")
    assert(elements2.map(_.text).mkString == "f2")
    page.content.update("f3", "f4")
    assert(elements1.map(_.text).mkString == "f3")
    assert(elements2.map(_.text).mkString == "f4")
  }

  it("should be able to fix a StaleElementReferenceException for div elements") {
    val page = to(StaleElementReferencePage())
    page.content.update("g1", "g2")
    val elements: Seq[UntypedElement] = page.content.div.elements
    assert(elements.map(_.text).mkString == "g1g2")
    page.content.update("g3", "g4")
    assert(elements.map(_.text).mkString == "g3g4")
  }
}
