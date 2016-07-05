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
 */
trait QueryDsl {
  /**
   * Returns an ID query.
   *
   * <p>
   * This method enables syntax such as the following:
   * </p>
   *
   * <pre class="stHighlight">
   * click on id("q")
   * &#94;
   * </pre>
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
   * <pre class="stHighlight">
   * click on name("q")
   * &#94;
   * </pre>
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
   * <pre class="stHighlight">
   * click on xpath("???")
   * &#94;
   * </pre>
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
   * <pre class="stHighlight">
   * click on className("???")
   * &#94;
   * </pre>
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
   * <pre class="stHighlight">
   * click on cssSelector("???")
   * &#94;
   * </pre>
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
   * <pre class="stHighlight">
   * click on linkText("???")
   * &#94;
   * </pre>
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
   * <pre class="stHighlight">
   * click on partialLinkText("???")
   * &#94;
   * </pre>
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
   * <pre class="stHighlight">
   * click on tagName("???")
   * &#94;
   * </pre>
   *
   *
   * @param tagName the query string for this query.
   */
  protected def tagName(tagName: String): TagNameQuery = TagNameQuery(tagName)
}
