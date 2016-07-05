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
package org.pageobject.core.api

import org.openqa.selenium.By

/**
 * A Custom Query Example.
 *
 * This is a simple example on how to implement a custom query
 */
case class QaTagQuery(prefix: String, qaTag: String) extends Query {
  val queryString = s"""$prefix[data-qa="$qaTag"]"""
  val by = By.cssSelector(queryString)
}

/**
 * A Custom QueryDsl Example.
 *
 * Example:
 * <code>
 *   case class Tour03GoogleSearchPage() extends PageObject {
 *     object content extends PageModule with QaTagDsl {
 *       private val example = textField(qaTag("exampleQaTag"))
 *       private val list = textField(qaTag("li", "exampleQaTag"))
 *     }
 *   }
 * </code>
 */
trait QaTagDsl {
  protected def qaTag(qaTag: String): QaTagQuery = {
    QaTagQuery("", qaTag)
  }

  protected def qaTag(prefix: String, qaTag: String): QaTagQuery = {
    QaTagQuery(prefix, qaTag)
  }
}
