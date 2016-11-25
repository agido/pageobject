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

import org.pageobject.core.api.Element
import org.pageobject.core.api.Locator

/**
 * This trait is part of the PageObject DSL.
 *
 * Example:
 * <code>
 *   object module extends PageModule {
 *     private def checkboxLocator = checkbox(tagName("input"))
 *
 *     def checkOption(): Unit = {
 *       click on checkboxLocator // <- this line is using the DSL
 *     }
 *   }
 * </code>
 */
trait ClickDsl {

  protected object click {
    /**
     * Click on the specified <code>Locator</code>
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
    def on[E <: Element](locator: Locator[E]): Unit = {
      locator.element.click()
    }

    def after(what: Click.ClickAfterEvent): Click.ClickAfterEventOn = what.on
  }

  object animation extends Click.Animation

}
