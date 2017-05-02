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
package org.pageobject.core.dsl

import org.pageobject.core.WaitFor.PatienceConfig
import org.pageobject.core.api.Element
import org.pageobject.core.api.Locator

object Click {

  /**
   * allows this DSL:
   *
   * <code>
   * click after /*==>*/ some_event /*<==*/ on locator
   * </code>
   */
  trait ClickAfterEvent {
    protected[dsl] def on: ClickAfterEventOn
  }

  trait ClickAfterEventOn extends DurationDsl {
    protected def patienceConfig: PatienceConfig

    def on[E <: Element](locator: Locator[E], patience: PatienceConfig = patienceConfig): Unit
  }

  /**
   * allows this DSL:
   *
   * <code>
   * click after /*==>*/ animation /*<==*/ on locator
   * </code>
   */
  protected[dsl] trait Animation extends ClickAfterEvent {

    object on extends ClickAfterEventOn {
      protected val patienceConfig: PatienceConfig = PatienceConfig(timeout = 5.seconds, interval = 500.milliseconds)

      /**
       * Click on the specified <code>Locator</code>
       *
       * Detects the location of the Element to click and waits `duration`.
       * After this the location is checked again.
       * The click is only processed if the location and size was not modified.
       * `AssertionError` is thrown otherwise.
       *
       * <code>
       *   trait ExampleModule extends PageModule {
       *     private val locator = $(id("example"))
       *
       *     def click(): Unit = {
       *       click on locator
       *     }
       *   }
       * </code>
       *
       * @param locator the <code>Locator</code> to click on
       */
      def on[E <: Element](locator: Locator[E], patienceConfig: PatienceConfig): Unit = {
        locator.element.clickAfterAnimation(patienceConfig)
      }
    }

  }

}
