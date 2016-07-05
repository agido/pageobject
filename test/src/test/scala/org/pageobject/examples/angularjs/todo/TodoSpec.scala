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

import org.pageobject.examples.ExamplePageObjectSpec

class TodoSpec extends ExamplePageObjectSpec {
  describe("AngularJs TODO App") {
    describe("the footer") {
      it("should initially not be visible") {
        Given("The AngularJs TODO App")
        val page = to(TodoPage())

        When("The TODO list is empty")
        assert(page.content.todoCount() == 0)

        Then("should the footer be not visible")
        assert(!page.content.isFooterVisible)
      }

      it("should be visible after adding a TODO") {
        Given("The AngularJs TODO App")
        val page = to(TodoPage())

        When("a TODO was added")
        page.content.addTodo(TodoTemplates.templates.head)

        Then("should the footer be visible")
        assert(page.content.isFooterVisible)
      }
    }

    describe("the TODO list") {
      it("should be possible to add TODOs") {
        Given("The AngularJs TODO App")
        val page = to(TodoPage())

        And("The current count of TODOs")
        val count = page.content.todoCount()

        When("a TODO was added")
        page.content.addTodo(TodoTemplates.templates.head)

        Then("should be one more TODO entry")
        assert(page.content.todoCount() == count + 1)
      }

      it("should be possible to add multiple TODOs") {
        Given("The AngularJs TODO App")
        val page = to(TodoPage())

        And("The current count of TODOs")
        val count = page.content.todoCount()

        When("multiple TODOs are added")
        TodoTemplates.templates.foreach(page.content.addTodo(_))

        Then("should be more TODO entries in the list")
        assert(page.content.todoCount() == count + TodoTemplates.templates.size)
      }

      it("should be possible to remove TODOs") {
        Given("The AngularJs TODO App")
        val page = to(TodoPage())

        And("At least one TODO")
        val todo = TodoTemplates.templates.head
        page.content.addTodo(todo)

        And("The current count of TODOs")
        val count = page.content.todoCount()

        When("the TODO was removed")
        page.content.removeTodo(todo)

        Then("should be one TODO entry less")
        assert(page.content.todoCount() == count - 1)
      }
    }
  }
}
