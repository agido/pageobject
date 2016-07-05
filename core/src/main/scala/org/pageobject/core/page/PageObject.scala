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

import org.pageobject.core.browser.PageHolder

/**
 * This trait is intended to be part of the testing pattern PageObject. Here it is implemented a bit different from
 * the basic PageObject pattern as depicted in many articles and books. In general a PageObject still represents a
 * page in the browser. But its content is composed by other traits and classes. A PageObject is always the outer
 * browser page itself. So a browser page is composed of a (root) PageObject which contains the actual content. Apart
 * from that it is possible to nest page into one another. Therefore for every PageObject its reference
 * to the encapsulating PageObject must be known. This is done using the <code>val parent</code>. In this context
 * an implicit variable is needed for every PageObject to pass its reference through to its children. Also an
 * implicit reference to the <code>WebDriver</code> access is needed in order to allow the content elements to be accessed by
 * functions provided by the modules. Last but not least a page defines its title. As it can be seen, the PageObject
 * trait just defines the outline of a page but not its real content, only that there is a content.
 * On the administrative level there is a private val which remembers the active page. If one wants access to
 * a content element on a non-active page an execption will be thrown using (<code>TestHelper.failTest()</code>). Therefore
 * content can only be defined within a PageObject.
 *
 * How to use a PageObject? This trait is extended with an AtChecker, a DefaultPageReference and a ParentPageReference.
 * The AtChecker only provides a function <code>atChecker(): Boolean</code> which is executed when the page is called
 * using the <code>to()</code> function. It also can be called seperately just to check if you landed on the correct page.
 * The <code>atChecker()</code> function must be implemented on every page which extends the PageObject trait. The
 * <code>atChecker()</code> function also cares about making the called page active such that elements on
 * it can be accessed.
 * The Default- and ParentPageReference are needed to model the tree of how the web site is built.
 *
 * A PageObject can contain PageModules, at least one <code>PageModule</code> named "content" is required. Within this the
 * actual content of what the web site look like is defined. Please note: There should be NO content definition at
 * all in PageObject. All content elements have to be declared in the content <code>PageModule</code>. Also the actions which
 * are offered to the user (like clicking, filling fields etc.) have to be defined in the <code>PageModule</code>s.
 *
 * If you want the PageObject to be called using a URL, you need to extend UrlPage trait in your page class. If you
 * furthermore have different domains your page you need to extend DomainPage trait.
 *
 * As already mentioned above, you need to activate a PageObject before you can execute Selenium commands inside of
 * the PageModules, otherwise an exception will be thrown. This can be done either by using <code>PageBrowser.to(page)</code>
 * in order to call a page url or by using <code>PageBrowser.at(page)</code> in order to check if landed on the
 * expected page.
 */
trait PageObject extends AtChecker with DefaultPageReference with ParentPageReference with PageBase {
  private val pageHolder = PageHolder()

  protected implicit def pageObject = this

  override protected[pageobject] val parent: Option[PageReference] = Some(this)

  override protected[pageobject] implicit def webDriver = {
    pageHolder.failOnInactivePage(this)
    pageHolder.webDriver
  }

  protected def invalidatePage(): Unit = {
    pageHolder.invalidatePage(this)
  }

  protected def pageTitle = webDriver.getTitle

}
