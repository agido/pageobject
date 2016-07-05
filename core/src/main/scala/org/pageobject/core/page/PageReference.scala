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
package org.pageobject.core.page

import org.openqa.selenium.WebDriver
import org.pageobject.core.api.Element
import org.pageobject.core.driver.DriverProvider

/**
 * A PageReference is used by PageModules to point to the parent PageObject or PageModule.
 *
 * It provides webDriver access, this is implemented for PageModules by delegating to
 * the parent and for PageObjects by delegating to the PageBrowser.
 *
 * All calls to Locator have an implicit PageReference, allowing the Locator to ask
 * for the Element to search in. When root returns None the full page will be searched.
 */
trait PageReference extends DriverProvider {
  /**
   * Returns the Element containg the PageModule.
   * PageObjects will always return None.
   *
   * @return Element containg the PageModule
   */
  protected[pageobject] def rootElement: Option[Element]

  /**
   * @return The parent PageModule or PageObject
   */
  protected[pageobject] def parent: Option[PageReference]
}

/**
 * Because each PageModule has two implicit PageReferences,
 * one pointing the PageModule itself, the other one pointing
 * to the parent PageReference, we need two types
 * to allow the compiler to decide which PageReference to use.
 *
 * This type is used to implicit pass "this" into Locator calls.
 */
trait OwnPageReference extends PageReference

/**
 * Because each PageModule has two implicit PageReferences,
 * one pointing the PageModule itself, the other one pointing
 * to the parent PageReference, we need two types
 * to allow the compiler to decide which PageReference to use.
 *
 * This type is used to implicit pass "parent" into the
 * PageModule constructor, preventing boilerplace code.
 */
trait ParentPageReference extends PageReference

/**
 * Helper trait needed to work with locators directly in Suites
 * without using PageObjects.
 *
 * Normally you don't need this trait.
 *
 * You should always prefer to use PageObjects in you Suites.
 */
trait PageReferenceProvider extends DriverProvider {
  implicit val pageReference = new DefaultPageReference with OwnPageReference {
    override protected[pageobject] implicit def webDriver: WebDriver = PageReferenceProvider.this.webDriver
  }
}

/**
 * Default implementation for PageReference, used by PageObject and PageReferenceProvider.
 */
trait DefaultPageReference extends PageReference {
  override protected[pageobject] def rootElement: Option[Element] = None

  override protected[pageobject] def parent: Option[PageReference] = None

  protected[pageobject] implicit def webDriver: WebDriver = parent.get.webDriver
}

object DefaultPageReference {
  def apply(root: Option[Element])(implicit driver: WebDriver): DefaultPageReference = {
    new DefaultPageReference() {
      protected[pageobject] override val rootElement: Option[Element] = root

      protected[pageobject] override implicit val webDriver: WebDriver = driver
    }
  }
}
