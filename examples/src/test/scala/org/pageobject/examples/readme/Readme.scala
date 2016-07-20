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
package org.pageobject.examples.readme

import org.pageobject.core.JettySuite.JettyPage
import org.pageobject.core.page.PageModule
import org.pageobject.examples.ExampleJettyPageObjectSpec

/**
 * The is a PageObjectSuite for the ReadmePage example.
 */
class Readme extends ExampleJettyPageObjectSpec {

  /**
   * ReadmePage is a PageObject because JettyPage extends PageObject.
   *
   * JettyPage's will be served by a local jetty server,
   * because of this only a local path is needed.
   */
  case class ReadmePage() extends JettyPage {
    val path: String = "/readme.html"

    /**
     * content is a PageModule
     */
    object content extends PageModule {
      // PageModules can access the DOM.
      private val readme = $(id("readme"))

      def readmeText: String = readme.text
    }

    // we assume that the browser is located
    // at the readme page when the page title is "Readme"
    def atChecker(): Boolean = pageTitle == "Readme"
  }

  describe("a small readme page") {
    it("should contain the text 'readme...'") {
      val page = to(ReadmePage())
      assert(page.content.readmeText == "readme...")
    }
  }
}
