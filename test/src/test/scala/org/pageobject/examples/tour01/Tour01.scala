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
package org.pageobject.examples.tour01

import org.scalatest.FunSpec
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.selenium.Driver
import org.scalatest.selenium.WebBrowser

trait Tour01 extends FunSpec with Eventually with IntegrationPatience {
  this: WebBrowser with Driver =>

  describe("google.com") {
    it("should change its title based on the term searched") {
      // Cancel test when cannot access google.com
      try goTo("http://www.google.com") catch {
        // this is very very evil...  we will fix this in Tour03
        case e: Throwable => cancel(e)
      }
      clickOn("q")
      textField("q").value = "Cheese!"
      submit()
      // Google's search is rendered dynamically with JavaScript.
      eventually(assert(pageTitle.startsWith("Cheese! - Google")))
      close()
    }
  }
}

// class Tour01WithChrome extends Tour01 with org.scalatest.selenium.Chrome

// class Tour01WithSafari extends Tour01 with org.scalatest.selenium.Safari

// class Tour01WithInternetExplorer extends Tour01 with org.scalatest.selenium.InternetExplorer

// class Tour01WithFirefox extends Tour01 with org.scalatest.selenium.Firefox
