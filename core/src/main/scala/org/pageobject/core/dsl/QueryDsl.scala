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

import org.pageobject.core.api.ClassNameQuery
import org.pageobject.core.api.CssSelectorQuery
import org.pageobject.core.api.IdQuery
import org.pageobject.core.api.LinkTextQuery
import org.pageobject.core.api.NameQuery
import org.pageobject.core.api.PartialLinkTextQuery
import org.pageobject.core.api.TagNameQuery
import org.pageobject.core.api.XPathQuery

/**
 * This trait is part of the PageObject DSL.
 *
 * This trait implements helper functions to create Query instances.
 *
 * <code>
 * trait ExampleModule extends PageModule {
 *   // $ -> UntypedLocator, we expect a div or something
 *   // linkText -> we select elements by linkText
 *   private val foo = $(linkText("foo"))
 *
 *   def click(): Unit = {
 *     // foo.element is invoked here,
 *     // the test will fail if no element (or more then one) is found
 *     click on foo
 *   }
 *
 *   // other example
 *   // singleSel -> we expect a single selection dropdown form element
 *   // cssSelector -> we select Elements by using a css query
 *   private val foo2 = singleSel(cssSelector("#someid .foo"))
 * }
 * </code>
 */
trait QueryDsl {
  /**
   * Returns an ID query.
   *
   * <p>
   * This method enables syntax such as the following:
   * </p>
   *
   * <code>
   * private val foo = $(id("foo"))
   * </code>
   *
   * id("someid") will select the same elements like cssSelector("#someid").
   * See also the example in documentation of trait QueryDsl.
   *
   *
   * @param elementId the query string for this query.
   */
  protected def id(elementId: String): IdQuery = IdQuery(elementId)

  /**
   * Returns a name query.
   *
   * <p>
   * This method enables syntax such as the following:
   * </p>
   *
   * <code>
   * private val foo = $(name("foo"))
   * </code>
   *
   * See also the example in documentation of trait QueryDsl.
   *
   *
   * @param elementName the query string for this query.
   */
  protected def name(elementName: String): NameQuery = NameQuery(elementName)

  /**
   * Returns an XPath query.
   *
   * <p>
   * This method enables syntax such as the following:
   * </p>
   *
   * <code>
   * private val foo = $(xpath("foo"))
   * </code>
   *
   * See also the example in documentation of trait QueryDsl.
   *
   *
   * @param xpath the query string for this query.
   */
  protected def xpath(xpath: String): XPathQuery = XPathQuery(xpath)

  /**
   * Returns a class name query.
   *
   * <p>
   * This method enables syntax such as the following:
   * </p>
   *
   * <code>
   * private val foo = $(className("foo"))
   * </code>
   *
   * className("class") will select the same elements like cssSelector(".class").
   * See also the example in documentation of trait QueryDsl.
   *
   *
   * @param className the query string for this query.
   */
  protected def className(className: String): ClassNameQuery = ClassNameQuery(className)

  /**
   * Returns a CSS selector query.
   *
   * <p>
   * This method enables syntax such as the following:
   * </p>
   *
   * <code>
   * private val foo = $(cssSelector("foo"))
   * </code>
   *
   * See also the example in documentation of trait QueryDsl.
   *
   *
   * @param cssSelector the query string for this query.
   */
  protected def cssSelector(cssSelector: String): CssSelectorQuery = CssSelectorQuery(cssSelector)

  /**
   * Returns a link text query.
   *
   * <p>
   * This method enables syntax such as the following:
   * </p>
   *
   * <code>
   * private val foo = $(linkText("foo"))
   * </code>
   *
   * See also the example in documentation of trait QueryDsl.
   *
   *
   * @param linkText the query string for this query.
   */
  protected def linkText(linkText: String): LinkTextQuery = LinkTextQuery(linkText)

  /**
   * Returns a partial link text query.
   *
   * <p>
   * This method enables syntax such as the following:
   * </p>
   *
   * <code>
   * private val foo = $(partialLinkText("foo"))
   * </code>
   *
   * See also the example in documentation of trait QueryDsl.
   *
   *
   * @param partialLinkText the query string for this query.
   */
  protected def partialLinkText(partialLinkText: String): PartialLinkTextQuery = PartialLinkTextQuery(partialLinkText)

  /**
   * Returns a tag name query.
   *
   * <p>
   * This method enables syntax such as the following:
   * </p>
   *
   * <code>
   * private val foo = $(tagName("foo"))
   * </code>
   *
   * tagName("div") will select the same elements like cssSelector("div").
   * See also the example in documentation of trait QueryDsl.
   *
   *
   * @param tagName the query string for this query.
   */
  protected def tagName(tagName: String): TagNameQuery = TagNameQuery(tagName)
}
