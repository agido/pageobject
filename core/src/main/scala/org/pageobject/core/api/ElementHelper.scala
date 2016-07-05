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
package org.pageobject.core.api

import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select

private[pageobject] object ElementHelper {
  def isElement(tag: String): WebElement => Boolean = _.getTagName.toLowerCase == tag

  val isSelectElement = isElement("select")

  val isInputElement = isElement("input")

  val isTextAreaElement = isElement("textarea")

  val isButtonElement = isElement("button")

  def isButton(webElement: WebElement): Boolean = isButtonElement(webElement) ||
    isInputField(webElement, "button") || isInputField(webElement, "reset") || isInputField(webElement, "submit")

  def isInputField(webElement: WebElement, name: String): Boolean =
    isInputElement(webElement) && webElement.getAttribute("type").toLowerCase == name

  def isTextField(webElement: WebElement): Boolean = isInputField(webElement, "text")

  def isPasswordField(webElement: WebElement): Boolean = isInputField(webElement, "password")

  def isCheckBox(webElement: WebElement): Boolean = isInputField(webElement, "checkbox")

  def isRadioButton(webElement: WebElement): Boolean = isInputField(webElement, "radio")

  def isEmailField(webElement: WebElement): Boolean = isInputField(webElement, "email") || isTextField(webElement)

  def isColorField(webElement: WebElement): Boolean = isInputField(webElement, "color") || isTextField(webElement)

  def isDateField(webElement: WebElement): Boolean = isInputField(webElement, "date") || isTextField(webElement)

  def isDateTimeField(webElement: WebElement): Boolean = isInputField(webElement, "datetime") || isTextField(webElement)

  def isDateTimeLocalField(webElement: WebElement): Boolean =
    isInputField(webElement, "datetime-local") || isTextField(webElement)

  def isMonthField(webElement: WebElement): Boolean = isInputField(webElement, "month") || isTextField(webElement)

  def isNumberField(webElement: WebElement): Boolean = isInputField(webElement, "number") || isTextField(webElement)

  def isRangeField(webElement: WebElement): Boolean = isInputField(webElement, "range") || isTextField(webElement)

  def isSearchField(webElement: WebElement): Boolean = isInputField(webElement, "search") || isTextField(webElement)

  def isTelField(webElement: WebElement): Boolean = isInputField(webElement, "tel") || isTextField(webElement)

  def isTimeField(webElement: WebElement): Boolean = isInputField(webElement, "time") || isTextField(webElement)

  def isUrlField(webElement: WebElement): Boolean = isInputField(webElement, "url") || isTextField(webElement)

  def isWeekField(webElement: WebElement): Boolean = isInputField(webElement, "week") || isTextField(webElement)

  def isTextArea(webElement: WebElement): Boolean = isTextAreaElement(webElement)

  def createTypedElement(factory: ElementFactory): Element = // scalastyle:ignore cyclomatic.complexity
  {
    val element = factory.initial
    if (isTextField(element)) {
      new TextField(factory)
    } else if (isTextArea(element)) {
      new TextArea(factory)
    } else if (isPasswordField(element)) {
      new PasswordField(factory)
    } else if (isEmailField(element)) {
      new EmailField(factory)
    } else if (isColorField(element)) {
      new ColorField(factory)
    } else if (isDateField(element)) {
      new DateField(factory)
    } else if (isDateTimeField(element)) {
      new DateTimeField(factory)
    } else if (isDateTimeLocalField(element)) {
      new DateTimeLocalField(factory)
    } else if (isMonthField(element)) {
      new MonthField(factory)
    } else if (isNumberField(element)) {
      new NumberField(factory)
    } else if (isRangeField(element)) {
      new RangeField(factory)
    } else if (isSearchField(element)) {
      new SearchField(factory)
    } else if (isTelField(element)) {
      new TelField(factory)
    } else if (isTimeField(element)) {
      new TimeField(factory)
    } else if (isUrlField(element)) {
      new UrlField(factory)
    } else if (isWeekField(element)) {
      new WeekField(factory)
    } else if (isCheckBox(element)) {
      new Checkbox(factory)
    } else if (isRadioButton(element)) {
      new RadioButton(factory)
    } else if (isSelectElement(element)) {
      val select = new Select(element)
      if (select.isMultiple) {
        new MultiSel(factory)
      } else {
        new SingleSel(factory)
      }
    } else {
      new UntypedElement(factory)
    }
  }
}
