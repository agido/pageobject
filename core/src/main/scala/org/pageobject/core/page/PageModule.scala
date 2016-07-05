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

import org.pageobject.core.TestHelper
import org.pageobject.core.api.Element
import org.pageobject.core.api.Query
import org.pageobject.core.api.UntypedLocator
import org.pageobject.core.driver.DriverProvider
import org.pageobject.core.dsl.BrowserPageDsl

/**
 * A PageModule represents a logical unit inside a <code>PageObject</code>. Speaking of a web site this is a
 * part of elements which are all closely related to each other such as the fields of a login box (username
 * and password) and the button to submit the information in order to log in. Apart from the definition of the
 * elements the PageModule also defines the actions possible within this part of the web site. Therefore access
 * to the web driver is needed. This access is provided by the implicit variable of webDriver which comes from
 * the encapsulating <code>PageObject</code>.
 *
 * On the technical side a PageModule has a reference to itself which is needed to get access to the elements
 * defined in the module. There is also the parent module (the encapsulating module or page) and the root element.
 * In order to get all children of this PageModule which satisfy a certain definition (in this case the definition
 * has the form of a <code>Query</code>), you can call <code>modules[T <: ElementPageModule](query: Query, factory: Element => T): Seq[T]</code>
 * function.
 *
 * A PageModule can contain other PageModules and has an implicit reference to the "parent",
 * either a <code>PageObject</code> or a <code>PageModule</code>.
 *
 * There are 2 special cases for using the PageModule pattern:
 * 1. The PageModule consists only of exactly one <code>Element</code>. If the PageModule is chained to an <code>Element</code>,
 * you should use <code>ElementPageModule</code> insteade of PageModule.
 * 2. The PageModule consists of all <code>Element</code>s returned by a <code>Query</code>. Such a PageModule can
 * be chained to a Query, use <code>QueryPageModule</code> insteade of PageModule.
 */
abstract class PageModule(implicit parentPageReference: ParentPageReference) extends BrowserPageDsl
  with DriverProvider with OwnPageReference with PageBase {

  protected implicit val ownPageReference: OwnPageReference = this

  override protected[pageobject] def rootElement: Option[Element] = parent.flatMap(_.rootElement)

  override protected[pageobject] implicit def parent = Some(parentPageReference)

  override protected[pageobject] implicit def webDriver = parent.get.webDriver
}

/**
 * A trait representing a PageModule chained to an Element.
 *
 * Example:
 * <code>
 *   case class TodoEntryModule(element: Element)(implicit parent: ParentPageReference) extends ElementPageModule
 * </code>
 */
trait ElementPageModule extends PageModule {
  /**
   * The Element this PageModule was chained to.
   */
  protected def element: Element

  /**
   * If you use a Locator in this PageModule, the Locator will only find child elements of this element.
   */
  override protected[pageobject] def rootElement: Option[Element] = Some(element)
}

/**
 * Use this class if you want to create a PageModule chained to a Query.
 *
 * It is required that the given Query will only return one Element, otherwise an Exception will be thrown.
 *
 * If you want to create a list of PageModules, for every Element returned by Query, use PageBase.modules,
 * implemented by PageObject and PageModule.
 *
 * This class will requery the element every time it is accessed. Also see FixedQueryPageModule.
 *
 * Example:
 * <code>
 *   class FooterModule(implicit parent: ParentPageReference) extends DynamicQueryPageModule(IdQuery("footer"))
 * </code>
 *
 * @param pageReferenceQuery A Query to execute to extract the Element
 *
 * @param parent implicit reference to the parent PageObject or PageModule
 */
abstract class DynamicQueryPageModule(val pageReferenceQuery: Query)(implicit parent: ParentPageReference)
  extends ElementPageModule {

  protected def element: Element = {
    UntypedLocator(pageReferenceQuery, parent).elementOption.getOrElse(
      TestHelper.failTest(s"""DynamicQueryPageModule("$pageReferenceQuery"): No Element found for Query"""))
  }
}

/**
 * Use this class if you want to create a PageModule chained to a Query.
 *
 * It is required that the given Query will only return one Element, otherwise an Exception will be thrown.
 *
 * If you want to create a list of PageModules, for every Element returned by Query, use PageBase.modules,
 * implemented by PageObject and PageModule.
 *
 * This class will query the element once and store it.
 * When the Element will be removed from the DOM a StaleElementReferenceException will be thrown.
 *
 * Example:
 * <code>
 *   class FooterModule(implicit parent: ParentPageReference) extends FixedQueryPageModule(IdQuery("footer"))
 * </code>
 *
 * @param pageReferenceQuery A Query to execute to extract the Element
 *
 * @param parent implicit reference to the parent PageObject or PageModule
 */
abstract class FixedQueryPageModule(pageReferenceQuery: Query)(implicit parent: ParentPageReference)
  extends ElementPageModule {

  protected val element: Element = {
    UntypedLocator(pageReferenceQuery, parent).elementOption.getOrElse(
      TestHelper.failTest(s"""FixedQueryPageModule("$pageReferenceQuery"): No Element found for Query"""))
  }
}
