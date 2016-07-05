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
package org.pageobject.core.dsl

import org.openqa.selenium.WebDriver
import org.pageobject.core.api.ButtonLocator
import org.pageobject.core.api.CheckboxLocator
import org.pageobject.core.api.ColorFieldLocator
import org.pageobject.core.api.DateFieldLocator
import org.pageobject.core.api.DateTimeFieldLocator
import org.pageobject.core.api.DateTimeLocalFieldLocator
import org.pageobject.core.api.EmailFieldLocator
import org.pageobject.core.api.HtmlLocator
import org.pageobject.core.api.MonthFieldLocator
import org.pageobject.core.api.MultiSelLocator
import org.pageobject.core.api.NumberFieldLocator
import org.pageobject.core.api.PasswordFieldLocator
import org.pageobject.core.api.Query
import org.pageobject.core.api.RadioButtonGroup
import org.pageobject.core.api.RadioButtonLocator
import org.pageobject.core.api.RangeFieldLocator
import org.pageobject.core.api.SearchFieldLocator
import org.pageobject.core.api.SingleSelLocator
import org.pageobject.core.api.TelFieldLocator
import org.pageobject.core.api.TextAreaLocator
import org.pageobject.core.api.TextFieldLocator
import org.pageobject.core.api.TimeFieldLocator
import org.pageobject.core.api.UntypedLocator
import org.pageobject.core.api.UrlFieldLocator
import org.pageobject.core.api.WeekFieldLocator
import org.pageobject.core.page.OwnPageReference

import scala.language.implicitConversions

trait LocatorDsl {
  protected def html(tag: String)(query: Query)(implicit reference: OwnPageReference): HtmlLocator = {
    HtmlLocator(tag, query, reference)
  }

  protected def div(query: Query)(implicit reference: OwnPageReference): HtmlLocator = html("div")(query: Query)

  protected def span(query: Query)(implicit reference: OwnPageReference): HtmlLocator = html("span")(query: Query)

  protected def a(query: Query)(implicit reference: OwnPageReference): HtmlLocator = html("a")(query: Query)

  /**
   * Finds and returns the first DOM Element selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code> if element not found or more then one element was found.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>Untyped</code> element selected by this query
   */
  protected def $(query: Query)(implicit reference: OwnPageReference): UntypedLocator = // scalastyle:ignore method.name
  {
    UntypedLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>button</code> or <code>input type="button"</code>
   * selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code> if element not found or the found element is not a button.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>TextField</code> selected by this query
   */
  protected def button(query: Query)(implicit reference: OwnPageReference): ButtonLocator = {
    ButtonLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>TextField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code> if element not found or the found element is not a <code>TextField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>TextField</code> selected by this query
   */
  protected def textField(query: Query)(implicit reference: OwnPageReference): TextFieldLocator = {
    TextFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>TextArea</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>TextArea</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>TextArea</code> selected by this query
   */
  protected def textArea(query: Query)(implicit reference: OwnPageReference): TextAreaLocator = {
    TextAreaLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>PasswordField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>PasswordField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>PasswordField</code> selected by this query
   */
  protected def passwordField(query: Query)(implicit reference: OwnPageReference): PasswordFieldLocator = {
    PasswordFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>EmailField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>EmailField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>EmailField</code> selected by this query
   */
  protected def emailField(query: Query)(implicit reference: OwnPageReference): EmailFieldLocator = {
    EmailFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>ColorField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>ColorField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>ColorField</code> selected by this query
   */
  protected def colorField(query: Query)(implicit reference: OwnPageReference): ColorFieldLocator = {
    ColorFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>DateField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>DateField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>DateField</code> selected by this query
   */
  protected def dateField(query: Query)(implicit reference: OwnPageReference): DateFieldLocator = {
    DateFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>DateTimeField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>DateTimeField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>DateTimeField</code> selected by this query
   */
  protected def dateTimeField(query: Query)(implicit reference: OwnPageReference): DateTimeFieldLocator = {
    DateTimeFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>DateTimeLocalField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>DateTimeLocalField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>DateTimeLocalField</code> selected by this query
   */
  protected def dateTimeLocalField(query: Query)(implicit reference: OwnPageReference): DateTimeLocalFieldLocator = {
    DateTimeLocalFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>MonthField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>MonthField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>MonthField</code> selected by this query
   */
  protected def monthField(query: Query)(implicit reference: OwnPageReference): MonthFieldLocator = {
    MonthFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>NumberField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>NumberField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>NumberField</code> selected by this query
   */
  protected def numberField(query: Query)(implicit reference: OwnPageReference): NumberFieldLocator = {
    NumberFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>RangeField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>RangeField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>RangeField</code> selected by this query
   */
  protected def rangeField(query: Query)(implicit reference: OwnPageReference): RangeFieldLocator = {
    RangeFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>SearchField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>SearchField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>SearchField</code> selected by this query
   */
  protected def searchField(query: Query)(implicit reference: OwnPageReference): SearchFieldLocator = {
    SearchFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>TelField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>TelField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>TelField</code> selected by this query
   */
  protected def telField(query: Query)(implicit reference: OwnPageReference): TelFieldLocator = {
    TelFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>TimeField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>TimeField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>TimeField</code> selected by this query
   */
  protected def timeField(query: Query)(implicit reference: OwnPageReference): TimeFieldLocator = {
    TimeFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>UrlField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>UrlField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>UrlField</code> selected by this query
   */
  protected def urlField(query: Query)(implicit reference: OwnPageReference): UrlFieldLocator = {
    UrlFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>WeekField</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>WeekField</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>WeekField</code> selected by this query
   */
  protected def weekField(query: Query)(implicit reference: OwnPageReference): WeekFieldLocator = {
    WeekFieldLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>RadioButton</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>RadioButton</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>RadioButton</code> selected by this query
   */
  protected def radioButton(query: Query)(implicit reference: OwnPageReference): RadioButtonLocator = {
    RadioButtonLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>Checkbox</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>Checkbox</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>Checkbox</code> selected by this query
   */
  protected def checkbox(query: Query)(implicit reference: OwnPageReference): CheckboxLocator = {
    CheckboxLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>SingleSel</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>SingleSel</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>SingleSel</code> selected by this query
   */
  protected def singleSel(query: Query)(implicit reference: OwnPageReference): SingleSelLocator = {
    SingleSelLocator(query, reference)
  }

  /**
   * Finds and returns the first <code>MultiSel</code> selected by the specified <code>Query</code>,
   * calls <code>TestHelper.failTest</code>
   * if element not found or the found element is not a <code>MultiSel</code>.
   *
   * @param query the <code>Query</code> with which to search
   *
   * @return the <code>MultiSel</code> selected by this query
   */
  protected def multiSel(query: Query)(implicit reference: OwnPageReference): MultiSelLocator = {
    MultiSelLocator(query, reference)
  }

  /**
   * Finds and returns <code>RadioButtonGroup</code> selected by the specified group name,
   * calls <code>TestHelper.failTest</code> if no element with the specified group name is found,
   * or found any element with the specified group name but not a <code>RadioButton</code>
   *
   * @param groupName the group name with which to search
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   *
   * @return the <code>RadioButtonGroup</code> selected by this query
   */
  protected def radioButtonGroup(groupName: String)(implicit driver: WebDriver) = RadioButtonGroup(groupName, driver)
}
