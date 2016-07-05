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
import org.openqa.selenium.WebElement
import org.pageobject.core.api.Element

import scala.collection.JavaConverters.asScalaSetConverter

/**
 * This trait is part of the PageObject DSL.
 *
 * This trait implements window and frame management commands.
 */
trait WindowDsl {
  /**
   * Get an opaque handle to current active window that uniquely identifies it within the implicit driver instance.
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   */
  protected def windowHandle(implicit driver: WebDriver): String = driver.getWindowHandle

  /**
   * Get a set of window handles which can be used to iterate over all open windows
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   */
  protected def windowHandles(implicit driver: WebDriver): Set[String] = driver.getWindowHandles.asScala.toSet

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
   *
   *
   * @param nameOrHandle name or window handle of the window to switch to
   *
   * @return a WindowTarget instance
   */
  protected def window(nameOrHandle: String) = new SwitchTarget.WindowTarget(nameOrHandle)

  /**
   * This method supports switching to a frame by index in PageObject DSL.
   *
   * <p>
   * This class is enables the following syntax:
   * </p>
   *
   * <pre>
   * switch to frame(0)
   * &#94;
   * </pre>
   *
   *
   * @param index the index of frame to switch to
   *
   * @return a FrameIndexTarget instance
   */
  protected def frame(index: Int) = new SwitchTarget.FrameIndexTarget(index)

  /**
   * This method supports switching to a frame by name or ID in PageObject DSL.
   *
   * <p>
   * This class is enables the following syntax:
   * </p>
   *
   * <pre>
   * switch to frame("name")
   * &#94;
   * </pre>
   *
   *
   * @param nameOrId name or ID of the frame to switch to
   *
   * @return a FrameNameOrIdTarget instance
   */
  protected def frame(nameOrId: String) = new SwitchTarget.FrameNameOrIdTarget(nameOrId)

  /**
   * This method supports switching to a frame by web element in PageObject DSL.
   *
   * @param element <code>WebElement</code> which is contained in the frame to switch to
   *
   * @return a FrameWebElementTarget instance
   */
  protected def frame(element: WebElement) = new SwitchTarget.FrameWebElementTarget(element)

  /**
   * This method supports switching to a frame by element in PageObject DSL.
   *
   * @param element <code>Element</code> which is contained in the frame to switch to
   *
   * @return a FrameElementTarget instance
   */
  protected def frame(element: Element) = new SwitchTarget.FrameElementTarget(element)
}
