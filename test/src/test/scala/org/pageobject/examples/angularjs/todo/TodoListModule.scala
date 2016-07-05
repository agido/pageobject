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
package org.pageobject.examples.angularjs.todo

import org.pageobject.core.api.IdQuery
import org.pageobject.core.page.FixedQueryPageModule
import org.pageobject.core.page.ParentPageReference

class TodoListModule(implicit parent: ParentPageReference) extends FixedQueryPageModule(IdQuery("todo-list")) {
  protected def todos = modules(xpath("li"), TodoEntryModule(_))

  def remove(todo: String): Unit = {
    todos.find(_.value == todo).foreach(_.remove())
  }

  def count: Int = todos.size
}
