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

import org.openqa.selenium.By
import org.pageobject.core.JettySuite.JettyPage
import org.pageobject.core.TestSpec
import org.pageobject.core.dsl.BrowserPageDsl
import org.pageobject.core.page.PageModule

class SelectTest extends TestSpec with BrowserPageDsl {

  case class SelectTestPage() extends JettyPage {
    val path = "/select.html"

    object content extends PageModule {
      private[SelectTest] def select1 = singleSel(id("select1"))

      private[SelectTest] def select2 = multiSel(name("select2"))
    }

    override def atChecker() = pageTitle == "Select"
  }

  def createSelects(): (SingleSelLocator, MultiSelLocator) = {
    val page = to(SelectTestPage())
    (page.content.select1, page.content.select2)
  }

  it("should return the correct query") {
    val (select1, select2) = createSelects() // scalastyle:ignore field.name
    assert(select1.query == id("select1"))
    assert(select2.query == name("select2"))
    assert(select1.query.by == By.id("select1"))
    assert(select2.query.by == By.name("select2"))
  }

  it("should detect the correct elements count") {
    val (select1, select2) = createSelects() // scalastyle:ignore field.name
    assert(select1.elements.length == 1)
    assert(select2.elements.length == 1)
  }

  it("should detect the correct options count") {
    val (select1, select2) = createSelects() // scalastyle:ignore field.name
    assert(select1.options.size == 3)
    assert(select1.options.keySet == Set("option1", "option2", "option3"))
    assert(select2.options.size == 3)
    assert(select2.options.keySet == Set("option4", "option5", "option6"))
  }

  it("should be able to single select") {
    val (select1, _) = createSelects() // scalastyle:ignore field.name

    assert(select1.value == "option1")
    select1.value = "option2"
    assert(select1.value == "option2")
    click on select1.options("option3")
    assert(select1.value == "option3")
  }

  it("should be able to multi select") {
    val (_, select2) = createSelects() // scalastyle:ignore field.name

    assert(select2.values == Seq())
    select2.values = Seq("option4")
    assert(select2.values == Seq("option4"))
    select2.values = Seq("option4", "option6")
    assert(select2.values == Seq("option4", "option6"))
    select2.options.values.foreach(_.click())
    assert(select2.value == "option5")
  }
}
