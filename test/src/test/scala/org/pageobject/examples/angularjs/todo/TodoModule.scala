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

import org.pageobject.core.page.PageModule
import org.pageobject.core.page.ParentPageReference

class TodoModule(implicit parent: ParentPageReference) extends PageModule {

  private object header extends TodoHeaderModule

  private object footer extends TodoFooterModule

  private object main extends TodoMainModule

  def addTodo(todo: String): Unit = header.add(todo)

  def removeTodo(todo: String): Unit = main.list.remove(todo)

  def isFooterVisible: Boolean = footer.isVisible

  def todoCount(): Int = main.list.count
}
