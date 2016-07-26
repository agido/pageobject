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

import org.openqa.selenium.Alert
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.pageobject.core.TestHelper
import org.pageobject.core.api.Element
import org.pageobject.core.api.ElementFactory
import org.pageobject.core.api.ElementHelper

/**
 * This trait is part of the PageObject DSL.
 *
 * This object contains helper used to implement SwitchTargetDsl.
 */
object SwitchTarget {

  /**
   * This object is part of the PageObject DSL.
   *
   * <p>
   * This object enables syntax such as the following:
   * </p>
   *
   * <pre class="stHighlight">
   * switch to alertBox
   * &#94;
   * </pre>
   **/
  object Switch {
    /**
     * Switch to the specified <code>SwitchTarget</code>
     *
     * @param target the <code>SwitchTarget</code> to switch to
     *
     * @param driver the <code>WebDriver</code> with which to drive the browser
     *
     * @return instance of specified <code>SwitchTarget</code>'s type parameter
     */
    def to[T](target: SwitchTarget[T])(implicit driver: WebDriver): T = {
      target.switch(driver)
    }
  }

  /**
   * This abstract class supports switching in PageObject DSL.
   *
   * <p>
   * One subclass of <code>SwitchTarget</code> exists for each kind of target that
   * can be switched to: active element, alert box, default content, frame (indentified by index,
   * name or id, or enclosed element), and window.
   * </p>
   */
  abstract class SwitchTarget[T] {
    /**
     * Abstract method implemented by subclasses that represent "targets" to which the user can switch.
     *
     * @param driver the <code>WebDriver</code> with which to perform the switch
     */
    def switch(implicit driver: WebDriver): T
  }

  /**
   * This class supports switching to the currently active element in PageObject DSL.
   *
   * <p>
   * This class is enables the following syntax:
   * </p>
   *
   * <pre>
   * switch to activeElement
   * &#94;
   * </pre>
   **/
  class ActiveElementTarget extends SwitchTarget[Element] {
    /**
     * Switches the driver to the currently active element.
     *
     * @param driver the <code>WebDriver</code> with which to perform the switch
     */
    def switch(implicit driver: WebDriver): Element = {
      ElementHelper.createTypedElement(ElementFactory(() => driver.switchTo.activeElement))
    }
  }

  /**
   * This class supports switching to the alert box in PageObject DSL.
   *
   * <p>
   * This class is enables the following syntax:
   * </p>
   *
   * <pre>
   * switch to alertBox
   * &#94;
   * </pre>
   **/
  class AlertTarget extends SwitchTarget[Alert] {
    /**
     * Switches the driver to the currently active alert box.
     *
     * @param driver the <code>WebDriver</code> with which to perform the switch
     */
    def switch(implicit driver: WebDriver): Alert = {
      driver.switchTo.alert
    }
  }

  /**
   * This class supports switching to the default content in PageObject DSL.
   *
   * <p>
   * This class is enables the following syntax:
   * </p>
   *
   * <pre>
   * switch to defaultContent
   * &#94;
   * </pre>
   **/
  class DefaultContentTarget extends SwitchTarget[WebDriver] {
    /**
     * Switches the driver to the default content
     *
     * @param driver the <code>WebDriver</code> with which to perform the switch
     */
    def switch(implicit driver: WebDriver): WebDriver = {
      driver.switchTo.defaultContent
    }
  }

  /**
   * This class supports switching to a frame by index in PageObject DSL.
   *
   * <p>
   * This class is enables the following syntax:
   * </p>
   *
   * <pre>
   * switch to frame(0)
   * &#94;
   * </pre>
   **/
  class FrameIndexTarget(index: Int) extends SwitchTarget[WebDriver] {
    /**
     * Switches the driver to the frame at the index that was passed to the constructor.
     *
     * @param driver the <code>WebDriver</code> with which to perform the switch
     */
    def switch(implicit driver: WebDriver): WebDriver =
    try {
      driver.switchTo.frame(index)
    } catch {
      case e: org.openqa.selenium.NoSuchFrameException =>
        TestHelper.failTest(s"Frame at index '$index' not found.")
    }
  }

  /**
   * This class supports switching to a frame by name or ID in PageObject DSL.
   *
   * <p>
   * This class is enables the following syntax:
   * </p>
   *
   * <pre>
   * switch to frame("name")
   * &#94;
   * </pre>
   **/
  class FrameNameOrIdTarget(nameOrId: String) extends SwitchTarget[WebDriver] {
    /**
     * Switches the driver to the frame with the name or ID that was passed to the constructor.
     *
     * @param driver the <code>WebDriver</code> with which to perform the switch
     */
    def switch(implicit driver: WebDriver): WebDriver =
    try {
      driver.switchTo.frame(nameOrId)
    } catch {
      case e: org.openqa.selenium.NoSuchFrameException =>
        TestHelper.failTest(s"Frame with name or ID '$nameOrId' not found.")
    }
  }

  /**
   * This class supports switching to a frame by web element in PageObject DSL.
   */
  class FrameWebElementTarget(webElement: WebElement) extends SwitchTarget[WebDriver] {
    /**
     * Switches the driver to the frame containing the <code>WebElement</code> that was passed to the constructor.
     *
     * @param driver the <code>WebDriver</code> with which to perform the switch
     */
    def switch(implicit driver: WebDriver): WebDriver =
    try {
      driver.switchTo.frame(webElement)
    } catch {
      case e: org.openqa.selenium.NoSuchFrameException =>
        TestHelper.failTest(s"Frame element '$webElement' not found.")
    }
  }

  /**
   * This class supports switching to a frame by element in PageObject DSL.
   */
  class FrameElementTarget(element: Element) extends SwitchTarget[WebDriver] {
    /**
     * Switches the driver to the frame containing the <code>Element</code> that was passed to the constructor.
     *
     * @param driver the <code>WebDriver</code> with which to perform the switch
     */
    def switch(implicit driver: WebDriver): WebDriver =
    try {
      driver.switchTo.frame(element.underlying)
    } catch {
      case e: org.openqa.selenium.NoSuchFrameException =>
        TestHelper.failTest(s"Frame element '$element' not found.")
    }
  }

  /**
   * This class supports switching to a window by name or handle in PageObject DSL.
   *
   * <p>
   * This class is enables the following syntax:
   * </p>
   *
   * <pre>
   * switch to window(windowHandle)
   * &#94;
   * </pre>
   **/
  class WindowTarget(nameOrHandle: String) extends SwitchTarget[WebDriver] {
    /**
     * Switches the driver to the window with the name or ID that was passed to the constructor.
     *
     * @param driver the <code>WebDriver</code> with which to perform the switch
     */
    def switch(implicit driver: WebDriver): WebDriver =
    try {
      driver.switchTo.window(nameOrHandle)
    } catch {
      case e: org.openqa.selenium.NoSuchWindowException =>
        TestHelper.failTest(s"Window with nameOrHandle '$nameOrHandle' not found.")
    }
  }

}
