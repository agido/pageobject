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
import org.pageobject.scalatest.JettySuite.JettyPage
import org.pageobject.core.TestSpec
import org.pageobject.core.dsl.BrowserPageDsl
import org.pageobject.core.page.PageModule

class RadioButtonTest extends TestSpec with BrowserPageDsl {

  case class RadioButtonTestPage() extends JettyPage {
    val path = "/radio.html"

    object content extends PageModule {
      private[RadioButtonTest] def buttons = radioButton(name("group1"))

      private[RadioButtonTest] def group = radioButtonGroup("group1")
    }

    override def atChecker() = pageTitle == "Radio Button"
  }

  def createRadioButton(): (RadioButtonLocator, RadioButtonGroup) = {
    val page = to(RadioButtonTestPage())
    (page.content.buttons, page.content.group)
  }

  it("should return the correct query") {
    val (buttons, _) = createRadioButton()
    assert(buttons.query == name("group1"))
    assert(buttons.query.by == By.name("group1"))
  }

  it("should detect the correct elements count") {
    val (buttons, _) = createRadioButton()
    assert(buttons.elements.length == 3)
  }

  it("should detect the correct options count") {
    val (_, group) = createRadioButton()
    val options = group.options
    assert(options.size == 3)
    assert(options.keySet == Set("Option 1", "Option 2", "Option 3"))
  }

  it("should be able to select") {
    val (_, group) = createRadioButton()
    val options = group.options

    assert(group.selection.isEmpty)

    group.value = "Option 1"
    assert(group.selection.get == "Option 1")

    click on options("Option 3")
    assert(group.selection.get == "Option 3")
  }
}
