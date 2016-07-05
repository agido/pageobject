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

import org.pageobject.core.api.Element
import org.pageobject.core.api.Query
import org.pageobject.core.api.UntypedLocator
import org.pageobject.core.dsl.QueryDsl

/**
 * Base trait for PageObject and PageModule.
 */
trait PageBase extends QueryDsl {
  this: PageReference =>

  /**
   * Creates a Seq of ElementPageModules, one module for each Element returned by Query.
   *
   * Example:
   * <code>
   *   def todos = modules(xpath("li"), TodoEntryModule(_))
   * </code>
   *
   * @param query the query to execute
   *
   * @param factory A function returning a ElementPageModule for the given Element.
   *
   * @tparam T the Type of the ElementPageModule
   *
   * @return A Seq of created ElementPageModules, can be empty.
   */
  protected def modules[T <: ElementPageModule](query: Query, factory: Element => T): Seq[T] = {
    UntypedLocator(query, this).elements.map(factory(_))
  }
}
