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

import java.util.concurrent.atomic.AtomicReference

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select
import org.pageobject.core.Dimension
import org.pageobject.core.Point
import org.pageobject.core.Rect
import org.pageobject.core.TestHelper
import org.pageobject.core.WaitFor
import org.pageobject.core.WaitFor.PatienceConfig
import org.pageobject.core.dsl.RetryHelper
import org.pageobject.core.dsl.ScriptDsl
import org.pageobject.core.page.DefaultPageReference
import org.pageobject.core.tools.Logging

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.immutable

/**
 * Wrapper class for a Selenium <code>WebElement</code>. This class provides all possibilities on a <code>WebElement</code>
 * which are also provided by Selenium. That contains information about the <code>WebElement</code> like its size,
 * location or css style but also all user interactions like clicking, filling, submitting etc.
 *
 * Apart from that there is a special <code>WebElement</code> class for a lot of different types of elements (the
 * most common ones). If you are sure about the element type you want to access, you could use the approtiate
 * case class like <code>DateTimeField</code> or <code>TextField</code>. Using these the locator of the element not
 * only identifies it but also checks if the element's type is as expected. If you're unsure or your web site changes
 * regularly, you could also use <code>UntypedElement</code>. In that case the check if the element found matches
 * the expected type is left out.
 *
 * <p>
 * This class provides idiomatic Scala access to the services of an underlying <code>WebElement</code>.
 * You can access the wrapped <code>WebElement</code> via the <code>underlying</code> method.
 * </p>
 */
abstract class Element(typeDescription: String, checker: WebElement => Boolean) extends Logging with WaitFor with ScriptDsl {
  /**
   * The factory returning the underlying <code>WebElement</code> wrapped by this <code>Element</code>
   */
  protected[pageobject] val factory: ElementFactory

  private def check(webElement: WebElement): WebElement = {
    if (!checker(webElement)) {
      TestHelper.failTest(s"WebElement is not of expected type: $this")
    }
    webElement
  }

  private val underlyingReference = new AtomicReference[WebElement](check(factory.initial))

  protected[pageobject] def underlying: WebElement = underlyingReference.get()

  protected def retry[T](description: String, retryOn: (Throwable => Boolean)*)(what: => T): T = {
    RetryHelper(recover = () => {
      debug(s"retrying Element.$description")
      underlyingReference.set(check(factory.retry()))
    },
      retryOn = RetryHelper.join(
        RetryHelper.retryOnStaleElementReferenceException,
        RetryHelper.join(retryOn: _*)
      )) {
      what
    }
  }

  /**
   * webElement can be used to access the underlying selenium WebElement.
   *
   * @return the underlying WebElement
   */
  def webElement: WebElement = underlying

  /**
   * The XY location and width/height of this <code>Element</code>.
   *
   * <p>
   * This invokes <code>getRect</code> on the underlying <code>WebElement</code>.
   * </p>
   *
   * @return the location and size of this element on the page
   */
  def rect: Rect = retry("rect") {
    // runtime error 'unknown command'
    //val rect = underlying.getRect
    val location = underlying.getLocation
    val size = underlying.getSize
    Rect(location.x, location.y, size.width, size.height)
  }

  /**
   * The XY location of the top-left corner of this <code>Element</code>.
   *
   * <p>
   * This invokes <code>getLocation</code> on the underlying <code>WebElement</code>.
   * </p>
   *
   * @return the location of the top-left corner of this element on the page
   */
  def location: Point = retry("location") {
    val location = underlying.getLocation
    Point(location.getX, location.getY)
  }

  /**
   * The width/height size of this <code>Element</code>.
   *
   * <p>
   * This invokes <code>getSize</code> on the underlying <code>WebElement</code>.
   * </p>
   *
   * @return the size of the element on the page
   */
  def size: Dimension = retry("size") {
    val size = underlying.getSize
    Dimension(size.getWidth, size.getHeight)
  }

  /**
   * Indicates whether this <code>Element</code> is displayed.
   *
   * <p>
   * This invokes <code>isDisplayed</code> on the underlying <code>WebElement</code>.
   * </p>
   *
   * @return <code>true</code> if the element is currently displayed
   */
  def isDisplayed: Boolean = retry("isDisplayed") {
    underlying.isDisplayed
  }

  /**
   * Indicates whether this <code>Element</code> is enabled.
   *
   * <p>
   * This invokes <code>isEnabled</code> on the underlying <code>WebElement</code>, which
   * will generally return <code>true</code> for everything but disabled input elements.
   * </p>
   *
   * @return <code>true</code> if the element is currently enabled
   */
  def isEnabled: Boolean = retry("isEnabled") {
    underlying.isEnabled
  }

  /**
   * Indicates whether this <code>Element</code> is selected.
   *
   * <p>
   * This method, which invokes <code>isSelected</code> on the underlying <code>WebElement</code>,
   * is relevant only for input elements such as checkboxes, options in a single- or multiple-selection
   * list box, and radio buttons. For any other element it will simply return <code>false</code>.
   * </p>
   *
   * @return <code>true</code> if the element is currently selected or checked
   */
  def isSelected: Boolean = retry("isSelected") {
    underlying.isSelected
  }

  /**
   * The tag name of this element.
   *
   * <p>
   * This method invokes <code>getTagName</code> on the underlying <code>WebElement</code>.
   * Note it returns the name of the tag, not the value of the of the <code>name</code> attribute.
   * For example, it will return will return <code>"input"</code> for the element
   * <code>&lt;input name="city" /&gt;</code>, not <code>"city"</code>.
   * </p>
   *
   * @return the tag name of this element
   */
  def tagName: String = retry("tagName") {
    underlying.getTagName
  }

  /**
   * The value for the given css attribute of this element, wrapped in a <code>Some</code>,
   * or <code>None</code> if no such css attribute exists on this <code>Element</code>.
   *
   * <p>
   * This method invokes <code>getCssValue</code> on the underlying <code>WebElement</code>, passing in the
   * specified <code>name</code>.
   * </p>
   *
   * @return the attribute with the given name, wrapped in a <code>Some</code>, else <code>None</code>
   */
  def css(name: String): Option[String] = retry(s"""css("$name")""") {
    Option(underlying.getCssValue(name))
  }

  /**
   * The attribute value of the given attribute name of this element, wrapped in a <code>Some</code>,
   * or <code>None</code> if no such attribute exists on this <code>Element</code>.
   *
   * <p>
   * This method invokes <code>getAttribute</code> on the underlying <code>WebElement</code>, passing in the
   * specified <code>name</code>.
   * </p>
   *
   * @return the attribute with the given name, wrapped in a <code>Some</code>, else <code>None</code>
   */
  def attribute(name: String): Option[String] = retry(s"""attribute("$name")""") {
    Option(underlying.getAttribute(name))
  }

  /**
   * Returns the visible (<em>i.e.</em>, not hidden by CSS) text of this element, including sub-elements,
   * without any leading or trailing whitespace.
   *
   * @return the visible text enclosed by this element, or an empty string, if the element encloses no visible text
   */
  def text: String = retry("text") {
    Option(underlying.getText).getOrElse("")
  }

  /**
   * Returns the result of invoking <code>equals</code> on the underlying <code>Element</code>, passing
   * in the specified <code>other</code> object.
   *
   * @param other the object with which to compare for equality
   *
   * @return true if the passed object is equal to this one
   */
  override def equals(other: Any): Boolean = other match {
    case other: WebElement => other == underlying
    case other: Element => other.underlying == underlying
    case None => other == None
  }

  /**
   * Returns the result of invoking <code>hashCode</code> on the underlying <code>Element</code>.
   *
   * @return a hash code for this object
   */
  override def hashCode: Int = underlying.hashCode

  /**
   * Returns the result of invoking <code>toString</code> on the underlying <code>Element</code>.
   *
   * @return a string representation of this object
   */
  override def toString: String =
    s"""Element $typeDescription <$tagName type=${attribute("type").getOrElse("N/A")}> $underlying"""

  /**
   * Gets this field's value.
   *
   * <p>
   * This method invokes <code>getAttribute("value")</code> on the underlying <code>WebElement</code>.
   * </p>
   *
   * @return the field's value
   */
  def value: String = attribute("value").get

  /**
   * Sets this field's value.
   *
   * @param value the new value
   */
  def value_=(value: String): Unit = retry(s"""value = "$value"""") {
    underlying.clear()
    underlying.sendKeys(value)
  }

  /**
   * Send keys to the Element.
   *
   * @param value the keys to send
   */
  def sendKeys(value: String): Unit = retry(s"""sendKeys("$value")""") {
    underlying.sendKeys(value)
  }

  def hasFocus: Boolean = retry("hasFocus") {
    underlying == factory.webDriver.switchTo.activeElement
  }

  /**
   * Clears this element.
   */
  def clear(): Unit = retry("clear()") {
    underlying.clear()
  }

  /**
   * Clicks this element.
   */
  def click(): Unit = retry("click()", RetryHelper.retryOnClickFailed) {
    underlying.click()
  }

  /**
   * Clicks this element after animation has finished.
   *
   * Detects the location of the Element to click and waits `duration`.
   * After this the location is checked again.
   * The click is only processed if the location and size was not modified.
   * `AssertionError` is thrown otherwise.
   */
  def clickAfterAnimation(patienceConfig: PatienceConfig): Unit = retry("clickAfterAnimation", RetryHelper.retryOnClickFailed) {
    waitFor("clickAfterAnimation", patienceConfig) {
      val newRect = rect
      Thread.sleep(patienceConfig.interval.toMillis)
      if (newRect == rect) {
        underlying.click()
      } else {
        throw new AssertionError("animation still in progress")
      }
    }
  }

  /**
   * Submits the current form.
   */
  def submit(): Unit = retry("submit") {
    underlying.submit()
  }

  /**
   * scroll the element into the view
   */
  def scrollIntoView(): Unit = retry("scrollIntoView") {
    implicit val webDriver = factory.webDriver
    executeScript("arguments[0].scrollIntoView()", underlying)
  }
}

/**
 * The tag name is readonly according to W3C specification.
 *
 * You can use this trait to prevent calling
 * <code>underlying.getTagName</code> if you already know the tag name.
 */
trait FixedTagName {
  self: Element =>

  def fixedTagName: String

  final override def tagName: String = fixedTagName
}

trait InputTagName extends FixedTagName {
  self: Element =>

  def fixedTagName: String = "input"
}

case class UntypedElement(factory: ElementFactory)
  extends Element("untyped", _ => true)

case class HtmlElement(fixedTagName: String)(val factory: ElementFactory)
  extends Element(fixedTagName, ElementHelper.isElement(fixedTagName)) with FixedTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * button("q").value should be ("Cheese!")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing
 */
case class Button(factory: ElementFactory)
  extends Element("button", ElementHelper.isButton) with FixedTagName {
  override def fixedTagName = "button"
}

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * textField("q").value should be ("Cheese!")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a text field
 */
case class TextField(factory: ElementFactory)
  extends Element("text", ElementHelper.isTextField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * textArea("q").value should be ("Cheese!")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a text area
 */
case class TextArea(factory: ElementFactory)
  extends Element("text area", ElementHelper.isTextArea) with FixedTagName {
  override def fixedTagName = "textarea"
}

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * pwdField("q").value should be ("Cheese!")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a password field
 */
case class PasswordField(factory: ElementFactory)
  extends Element("password", ElementHelper.isPasswordField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * emailField("q").value should be ("foo@bar.com")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a email field
 */
case class EmailField(factory: ElementFactory)
  extends Element("email", ElementHelper.isEmailField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * colorField("q").value should be ("Cheese!")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a color field
 */
case class ColorField(factory: ElementFactory)
  extends Element("color", ElementHelper.isColorField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * dateField("q").value should be ("2003-03-01")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a date field
 */
case class DateField(factory: ElementFactory)
  extends Element("date", ElementHelper.isDateField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * dateTimeField("q").value should be ("2003-03-01T12:13:14")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a datetime field
 */
case class DateTimeField(factory: ElementFactory)
  extends Element("datetime", ElementHelper.isDateTimeField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * dateTimeLocalField("q").value should be ("2003-03-01T12:13:14")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a datetime-local field
 */
case class DateTimeLocalField(factory: ElementFactory)
  extends Element("datetime-local", ElementHelper.isDateTimeLocalField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * monthField("q").value should be ("2003-04")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a month field
 */
case class MonthField(factory: ElementFactory)
  extends Element("month", ElementHelper.isMonthField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * numberField("q").value should be ("1.3")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a number field
 */
case class NumberField(factory: ElementFactory)
  extends Element("number", ElementHelper.isNumberField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * rangeField("q").value should be ("1.3")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a range field
 */
case class RangeField(factory: ElementFactory)
  extends Element("range", ElementHelper.isRangeField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * searchField("q").value should be ("google")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a search field
 */
case class SearchField(factory: ElementFactory)
  extends Element("search", ElementHelper.isSearchField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * telField("q").value should be ("911-911-9191")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a tel field
 */
case class TelField(factory: ElementFactory)
  extends Element("tel", ElementHelper.isTelField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * timeField("q").value should be ("12:13:14")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a time field
 */
case class TimeField(factory: ElementFactory)
  extends Element("time", ElementHelper.isTimeField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * urlField("q").value should be ("http://google.com")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a url field
 */
case class UrlField(factory: ElementFactory)
  extends Element("url", ElementHelper.isUrlField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * weekField("q").value should be ("1996-W16")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a week field
 */
case class WeekField(factory: ElementFactory)
  extends Element("week", ElementHelper.isWeekField) with InputTagName

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * radioButton(id("opt1")).value should be ("Option 1!")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a text area
 */
case class RadioButton(factory: ElementFactory)
  extends Element("radio button", ElementHelper.isRadioButton) with InputTagName {

  override def value_=(value: String) = throw new NotImplementedError

  override def clear() = throw new NotImplementedError
}

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * checkbox("cbx1").select()
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a checkbox
 */
case class Checkbox(factory: ElementFactory)
  extends Element("check box", ElementHelper.isCheckBox) with InputTagName {

  /**
   * Selects this checkbox.
   */
  def select(): Unit = retry("select()") {
    if (!underlying.isSelected) {
      underlying.click()
    }
  }

  /**
   * Clears this checkbox
   */
  override def clear(): Unit = retry("clear()") {
    if (underlying.isSelected) {
      underlying.click()
    }
  }

  override def value_=(value: String) = throw new NotImplementedError
}

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * radioButtonGroup("group1").value should be ("Option 2")
 * </pre>
 */
case class RadioButtonGroup(groupName: String, driver: WebDriver) extends DefaultPageReference {

  protected[pageobject] override implicit val webDriver = driver

  private val groupElements = RadioButtonLocator(NameQuery(groupName), this)

  if (groupElements.elements.isEmpty) {
    TestHelper.failTest(s"No radio buttons with group name '$groupName' was found.")
  }

  def options: Map[String, RadioButton] = groupElements.asMap

  /**
   * Returns the value of this group's selected radio button, or throws <code>TestFailedException</code> if no
   * radio button in this group is selected.
   *
   * @return the value of this group's selected radio button
   */
  def value: String = selection.getOrElse(TestHelper.failTest(
    "The radio button group on which value was invoked contained no selected radio button."))

  /**
   * Returns the value of this group's selected radio button, wrapped in a <code>Some</code>, or <code>None</code>,
   * if no radio button in this group is selected.
   *
   * @return the value of this group's selected radio button, wrapped in a <code>Some</code>, else <code>None</code>
   */
  def selection: Option[String] = {
    groupElements.elements.find(_.isSelected) match {
      case Some(radio) =>
        Some(radio.value)
      case None =>
        None
    }
  }

  /**
   * Selects the radio button with the passed value.
   *
   * @param value the value of the radio button to select
   */
  def value_=(value: String): Unit = {
    groupElements.elements.find(_.value == value) match {
      case Some(radio) =>
        radio.click()
      case None =>
        TestHelper.failTest(s"Radio button value '$value' not found for group '$groupName'.")
    }
  }
}

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * multiSel("select2").values += "option5"
 * &#94;
 * </pre>
 *
 * <p>
 * Instances of this class are returned from the <code>values</code> method of <code>MultiSel</code>.
 * <code>MultiSelOptionSeq</code> is an immutable <code>IndexedSeq[String]</code> that wraps an underlying immutable
 * <code>IndexedSeq[String]</code> and adds two methods, <code>+</code> and <code>-</code>,
 * to facilitate the <code>+=</code> syntax for setting additional options of the <code>MultiSel</code>.
 *
 * The Scala compiler will rewrite:
 * </p>
 *
 * <pre class="stHighlight">
 * multiSel("select2").values += "option5"
 * </pre>
 *
 * <p>
 * To:
 * </p>
 *
 * <pre class="stHighlight">
 * multiSel("select2").values = multiSel("select2").values + "option5"
 * </pre>
 *
 * <p>
 * Thus, first a new <code>MultiSelOptionSeq</code> is created by invoking the <code>+</code> method on the
 * <code>MultiSelOptionSeq</code> returned by <code>values</code>, and that result is passed
 * to the <code>values_=</code> method.
 * </p>
 *
 * <p>
 * For symmetry, this class also offers a <code>-</code> method, which can be used to deselect an option, like this:
 * </p>
 *
 * <pre class="stHighlight">
 * multiSel("select2").values -= "option5"
 * &#94;
 * </pre>
 *
 */
class MultiSelOptionSeq(underlying: immutable.IndexedSeq[String]) extends immutable.IndexedSeq[String] {

  /**
   * Selects an element by its index in the sequence.
   *
   * <p>
   * This method invokes <code>apply</code> on the underlying immutable <code>IndexedSeq[String]</code>,
   * passing in <code>idx</code>, and returns the result.
   * </p>
   *
   * @param idx the index to select
   *
   * @return the element of this sequence at index <code>idx</code>, where 0 indicates the first element
   */
  def apply(idx: Int): String = underlying.apply(idx)

  /**
   * The length of this sequence.
   *
   * <p>
   * This method invokes <code>length</code> on the underlying immutable <code>IndexedSeq[String]</code>
   * and returns the result.
   * </p>
   *
   * @return the number of elements in this sequence
   */
  def length: Int = underlying.length

  /**
   * Appends a string element to this sequence, if it doesn't already exist in the sequence.
   *
   * <p>
   * If the string element already exists in this sequence, this method returns itself. If not,
   * this method returns a new <code>MultiSelOptionSeq</code> with the passed value appended to the
   * end of the original <code>MultiSelOptionSeq</code>.
   * </p>
   *
   * @param value the string element to append to this sequence
   *
   * @return a <code>MultiSelOptionSeq</code> that contains the passed string value
   */
  def +(value: String): MultiSelOptionSeq = // scalastyle:ignore method.name
  {
    if (!underlying.contains(value)) {
      new MultiSelOptionSeq(underlying :+ value)
    } else {
      this
    }
  }

  /**
   * Removes a string element to this sequence, if it already exists in the sequence.
   *
   * <p>
   * If the string element does not already exist in this sequence, this method returns itself. If the element
   * is contained in this sequence, this method returns a new <code>MultiSelOptionSeq</code> with the passed value
   * removed from the the original <code>MultiSelOptionSeq</code>, leaving any other elements in the same order.
   * </p>
   *
   * @param value the string element to append to this sequence
   *
   * @return a <code>MultiSelOptionSeq</code> that contains the passed string value
   */
  def -(value: String): MultiSelOptionSeq = // scalastyle:ignore method.name
  {
    if (underlying.contains(value)) {
      new MultiSelOptionSeq(underlying.filter(_ != value))
    } else {
      this
    }
  }
}

abstract class AbstractSel(typeDescription: String, isMultiple: Boolean)
  extends Element(typeDescription, ElementHelper.isSelectElement) with FixedTagName {

  final val fixedTagName = "select"

  private val optionLocator = UntypedLocator(TagNameQuery("option"),
    DefaultPageReference(Some(this))(factory.webDriver))

  val isMulti = attribute("multiple") != Option("false")

  protected def select = retry("select") {
    new Select(underlying)
  }

  if (select.isMultiple != isMultiple) {
    TestHelper.failTest(s"Element $underlying is not a $typeDescription list.")
  }

  def options: Map[String, Element] = optionLocator.asMap
}

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * singleSel.clear()
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a single selection list
 */
case class SingleSel(factory: ElementFactory)
  extends AbstractSel("single select", false) {

  /**
   * Returns the value of this single selection list, wrapped in a <code>Some</code>, or <code>None</code>,
   * if this single selection list has no currently selected value.
   *
   * @return the value of this single selection list, wrapped in a <code>Some</code>, else <code>None</code>
   */
  def selection: Option[String] = Option(select.getFirstSelectedOption).map(_.getAttribute("value"))

  /**
   * Gets this single selection list's selected value, or throws <code>TestFailedException</code>
   * if no value is currently selected.
   *
   * @return the single selection list's value
   */
  override def value: String = selection match {
    case Some(v) => v
    case None =>
      TestHelper.failTest("The single selection list on which value was invoked had no selection.")
  }

  /**
   * Sets this single selection list's value to the passed value.
   *
   * @param value the new value
   */
  override def value_=(value: String): Unit = {
    try {
      select.selectByValue(value)
    } catch {
      case e: org.openqa.selenium.NoSuchElementException => TestHelper.failTest(e)
    }
  }
}

/**
 * This class is part of the PageObject DSL.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * multiSel("select2").clear("option5")
 * </pre>
 *
 * @param factory the <code>ElementFactory</code> representing a multiple selection list
 */
case class MultiSel(factory: ElementFactory)
  extends AbstractSel("multi select", true) {

  /**
   * Clears the passed value in this multiple selection list.
   *
   * @param value the value to clear
   */
  def clear(value: String): Unit = {
    select.deselectByValue(value)
  }

  override def value_=(value: String): Unit = TestHelper.failTest("'value' can not be used to modify a MultiSel Element!")

  /**
   * Gets all selected values of this multiple selection list.
   *
   * <p>
   * If the multiple selection list has no selections, ths method will
   * return an empty <code>IndexedSeq</code>.
   * </p>
   *
   * @return An <code>IndexedSeq</code> containing the currently selected values
   */
  def values: MultiSelOptionSeq = {
    val elementSeq = Vector.empty ++ select.getAllSelectedOptions.asScala
    new MultiSelOptionSeq(elementSeq.map(_.getAttribute("value")))
  }

  /**
   * Clears any existing selections then sets all values contained in the passed <code>collection.Seq[String]</code>.
   *
   * <p>
   * In other words, the <code>values_=</code> method <em>replaces</em> the current selections, if any, with
   * new selections defined by the passed <code>Seq[String]</code>.
   * </p>
   *
   * @param values a <code>Seq</code> of string values to select
   */
  def values_=(values: collection.Seq[String]): Unit = {
    try {
      clearAll()
      values.foreach(select.selectByValue)
    } catch {
      case e: org.openqa.selenium.NoSuchElementException =>
        TestHelper.failTest(e.getMessage)
    }
  }

  /**
   * Clears all selected values in this multiple selection list.
   */
  def clearAll(): Unit = {
    select.deselectAll()
  }
}
