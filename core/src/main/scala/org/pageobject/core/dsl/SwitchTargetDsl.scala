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

/**
 * This trait is part of the PageObject DSL.
 *
 * This trait implements window switching commands.
 */
trait SwitchTargetDsl {
  protected val switch = SwitchTarget.Switch

  /**
   * Switch to the specified <code>SwitchTarget</code>
   *
   * @param target the <code>SwitchTarget</code> to switch to
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   *
   * @return instance of specified <code>SwitchTarget</code>'s type parameter
   */
  protected def switchTo[T](target: SwitchTarget.SwitchTarget[T])(implicit driver: WebDriver): T = switch to target

  /**
   * This value supports switching to the currently active element in PageObject DSL.
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
  protected val activeElement = new SwitchTarget.ActiveElementTarget()

  /**
   * This value supports switching to the alert box in PageObject DSL.
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
  protected val alertBox = new SwitchTarget.AlertTarget()

  /**
   * This value supports switching to the default content in PageObject DSL.
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
  protected val defaultContent = new SwitchTarget.DefaultContentTarget()
}
