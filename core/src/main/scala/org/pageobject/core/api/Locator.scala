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

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.pageobject.core.WaitFor
import org.pageobject.core.driver.DriverProvider
import org.pageobject.core.page.PageReference

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.language.implicitConversions

object Locator extends WaitFor {

  object SingleElement extends WaitFor.PatienceConfig(timeout = 10.seconds, interval = 500.milliseconds)

  implicit def locatorToElement[E <: Element](locator: Locator[E]): E = locator.element
}

/**
 * The Locator locates an <code>Element</code> due to a <code>Query</code>. If used there is either the possibility
 * to get exactly 1 element. In this case the <code>element: E</code> function should be called. If more than 1
 * element has to be found (like a list of li items) the <code>elements: Seq[E]</code> function should be called.
 *
 * @param elementFactory factory creating a new Element instance for the given WebElement
 *
 * @tparam E the type of Elements to locate
 */
abstract class Locator[E <: Element](elementFactory: ElementFactory => E) extends DriverProvider with WaitFor {
  protected val query: Query
  protected val reference: PageReference

  override implicit def webDriver: WebDriver = reference.webDriver

  private def singleElement[T](elements: Seq[T]): T = {
    assert(elements.size == 1)
    elements.head
  }

  private def singleElementOption[T](elements: Seq[T]): Option[T] = {
    assert(elements.isEmpty || elements.size == 1)
    elements.headOption
  }

  def anyDisplayed: Boolean = {
    elements.exists(_.isDisplayed)
  }

  def elements: Seq[E] = {
    val initial = webElements
    initial.zipWithIndex.map({
      case (element, i) =>
        def requery(): WebElement = {
          val query = webElements
          assert(initial.size == query.size)
          query(i)
        }

        elementFactory(ElementFactory(element, () => requery()))
    })
  }

  def element: E = waitFor("element", Locator.SingleElement) {
    elementFactory(ElementFactory(() => singleElement(webElements)))
  }

  def elementOption: Option[E] = {
    webElementOption.map(element => elementFactory(
      ElementFactory(element, () => singleElement(webElements)))
    )
  }

  def webElement: WebElement = waitFor("webElement", Locator.SingleElement) {
    singleElement(webElements)
  }

  def webElementOption: Option[WebElement] = waitFor("webElementOption", Locator.SingleElement) {
    singleElementOption(webElements)
  }

  def webElements: Seq[WebElement] = {
    reference.rootElement.fold(webDriver.findElements(query.by))(_.underlying.findElements(query.by)).asScala
  }

  def asMap: Map[String, E] = {
    elements.map(element => (element.value, element))(collection.breakOut)
  }
}

case class HtmlLocator(tag: String, query: Query, reference: PageReference) extends Locator(HtmlElement(tag))

case class UntypedLocator(query: Query, reference: PageReference) extends Locator(UntypedElement)

case class ButtonLocator(query: Query, reference: PageReference) extends Locator(Button)

case class TextFieldLocator(query: Query, reference: PageReference) extends Locator(TextField)

case class TextAreaLocator(query: Query, reference: PageReference) extends Locator(TextArea)

case class PasswordFieldLocator(query: Query, reference: PageReference) extends Locator(PasswordField)

case class EmailFieldLocator(query: Query, reference: PageReference) extends Locator(EmailField)

case class ColorFieldLocator(query: Query, reference: PageReference) extends Locator(ColorField)

case class DateFieldLocator(query: Query, reference: PageReference) extends Locator(DateField)

case class DateTimeFieldLocator(query: Query, reference: PageReference) extends Locator(DateTimeField)

case class DateTimeLocalFieldLocator(query: Query, reference: PageReference) extends Locator(DateTimeLocalField)

case class MonthFieldLocator(query: Query, reference: PageReference) extends Locator(MonthField)

case class NumberFieldLocator(query: Query, reference: PageReference) extends Locator(NumberField)

case class RangeFieldLocator(query: Query, reference: PageReference) extends Locator(RangeField)

case class SearchFieldLocator(query: Query, reference: PageReference) extends Locator(SearchField)

case class TelFieldLocator(query: Query, reference: PageReference) extends Locator(TelField)

case class TimeFieldLocator(query: Query, reference: PageReference) extends Locator(TimeField)

case class UrlFieldLocator(query: Query, reference: PageReference) extends Locator(UrlField)

case class WeekFieldLocator(query: Query, reference: PageReference) extends Locator(WeekField)

case class RadioButtonLocator(query: Query, reference: PageReference) extends Locator(RadioButton)

case class CheckboxLocator(query: Query, reference: PageReference) extends Locator(Checkbox)

case class SingleSelLocator(query: Query, reference: PageReference) extends Locator(SingleSel)

case class MultiSelLocator(query: Query, reference: PageReference) extends Locator(MultiSel)
