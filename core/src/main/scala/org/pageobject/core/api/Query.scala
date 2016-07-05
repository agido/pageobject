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
package org.pageobject.core.api

import org.openqa.selenium.By
import org.openqa.selenium.support.ByIdOrName

import scala.language.implicitConversions

/**
 * This trait is part of the PageObject DSL.
 *
 * <p>
 * Subclasses of this trait define different ways of querying for elements, enabling
 * syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * click on id("q")
 * &#94;
 * </pre>
 **/
trait Query {

  /**
   * The Selenium <code>By</code> for this query.
   */
  def by: By

  /**
   * The query string for this query.
   *
   * <p>
   * For example, the query string for <code>id("q")</code> is <code>"q"</code>.
   * </p>
   */
  def queryString: String
}

/**
 * An ID query.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * click on id("q")
 * &#94;
 * </pre>
 *
 *
 * @param queryString the query string for this query.
 */
case class IdQuery(queryString: String) extends Query {
  val by = By.id(queryString)
}

trait ImplicitIdQuery {
  implicit def stringToQuery(query: String): IdQuery = IdQuery(query)
}

/**
 * An ID query.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * click on id("q")
 * &#94;
 * </pre>
 *
 *
 * @param queryString the query string for this query.
 */
case class IdOrNameQuery(queryString: String) extends Query {
  val by = new ByIdOrName(queryString)
}

trait ImplicitIdOrNameQuery {
  implicit def stringToQuery(query: String): IdOrNameQuery = IdOrNameQuery(query)
}

/**
 * A name query.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * click on name("q")
 * &#94;
 * </pre>
 *
 *
 * @param queryString the query string for this query.
 */
case class NameQuery(queryString: String) extends Query {
  val by = By.name(queryString)
}

trait ImplicitNameQuery {
  implicit def stringToQuery(query: String): NameQuery = NameQuery(query)
}

/**
 * An XPath query.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * click on xpath("???")
 * &#94;
 * </pre>
 *
 *
 * @param queryString the query string for this query.
 */
case class XPathQuery(queryString: String) extends Query {
  val by = By.xpath(queryString)
}

trait ImplicitXPathQuery {
  implicit def stringToQuery(query: String): XPathQuery = XPathQuery(query)
}

/**
 * A class name query.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * click on className("???")
 * &#94;
 * </pre>
 *
 *
 * @param queryString the query string for this query.
 */
case class ClassNameQuery(queryString: String) extends Query {
  val by = By.className(queryString)
}

trait ImplicitClassNameQuery {
  implicit def stringToQuery(query: String): ClassNameQuery = ClassNameQuery(query)
}

/**
 * A CSS selector query.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * click on cssSelector("???")
 * &#94;
 * </pre>
 *
 *
 * @param queryString the query string for this query.
 */
case class CssSelectorQuery(queryString: String) extends Query {
  val by = By.cssSelector(queryString)
}

trait ImplicitCssSelectorQuery {
  implicit def stringToQuery(query: String): CssSelectorQuery = CssSelectorQuery(query)
}

/**
 * A link text query.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * click on linkText("???")
 * &#94;
 * </pre>
 *
 *
 * @param queryString the query string for this query.
 */
case class LinkTextQuery(queryString: String) extends Query {
  val by = By.linkText(queryString)
}

trait ImplicitLinkTextQuery {
  implicit def stringToQuery(query: String): LinkTextQuery = LinkTextQuery(query)
}

/**
 * A partial link text query.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * click on partialLinkText("???")
 * &#94;
 * </pre>
 *
 *
 * @param queryString the query string for this query.
 */
case class PartialLinkTextQuery(queryString: String) extends Query {
  val by = By.partialLinkText(queryString)
}

trait ImplicitPartialLinkTextQuery {
  implicit def stringToQuery(query: String): PartialLinkTextQuery = PartialLinkTextQuery(query)
}

/**
 * A tag name query.
 *
 * <p>
 * This class enables syntax such as the following:
 * </p>
 *
 * <pre class="stHighlight">
 * click on tagName("???")
 * &#94;
 * </pre>
 *
 *
 * @param queryString the query string for this query.
 */
case class TagNameQuery(queryString: String) extends Query {
  val by = By.tagName(queryString)
}

trait ImplicitTagNameQuery {
  implicit def stringToQuery(query: String): TagNameQuery = TagNameQuery(query)
}
