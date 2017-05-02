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
package org.pageobject.examples.tour02

import org.pageobject.core.WaitFor
import org.pageobject.core.api.ImplicitNameQuery
import org.pageobject.core.dsl.BrowserDsl
import org.pageobject.core.page.PageReferenceProvider
import org.pageobject.scalatest.PageObjectSuite
import org.scalatest.FunSpec

/**
 * If you are using WebBrowser inside of your test class (this is not recommended)
 * you also need to use trait DefaultDriverProvider.
 *
 * See next example for a simple page object example
 */
class Tour02 extends FunSpec with PageObjectSuite with BrowserDsl with WaitFor with ImplicitNameQuery
  with PageReferenceProvider {

  describe("google.com") {

    it("should change its title based on the term searched") {
      // Cancel test when cannot access google.com
      try goTo("http://www.google.com") catch {
        // this is very very evil... we will fix this in Tour03
        case e: Throwable => cancel(e)
      }
      textField("q").element.value = "Cheese!"
      submit()
      // Google's search is rendered dynamically with JavaScript.
      waitFor("example", timeout = 30.seconds, interval = 1.second) {
        assert(pageTitle.startsWith("Cheese! - Google"))
      }

      // look here, what if the test has failed and this code was never executed?
      // because we don't want to leave browser windows open DriverLauncher will close the window for you
      // close()
    }

  }
}
