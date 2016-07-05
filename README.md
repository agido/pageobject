# PageObject
A Page Object Pattern implementation written in Scala.

PageObject is a free, open-source ([Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt)) extension for test runners implementing the Page Object Pattern.
Currently only ScalaTest 3 is supported, but other testing frameworks should be easy to add.

Official Website: http://www.pageobject.org/

PageObject was initially based on [ScalaTest's Selenium Support](http://www.scalatest.org/user_guide/using_selenium).

## Contributing
Feel free to open an Issue if you think something is missing our you found a bug. Pull Requests are very welcome!

## How can PageObject help me?
PageObject will manage all selenium stuff for you, including starting a browser, on linux optionally using VNC.

You just have to write a description of the page you want to test and the test itself.

## PageObject Terms
A **PageModule** represents a part of a Page.
Only PageModule's can access the DOM, they should provide a public API used by the tests. A PageModule can contain other PageModules.

A **PageObject** represents a Page in the Browser.
A PageObject can contain PageModules (for example "navigation", "content" and "footer").

Use a **PageObjectSuite** to interact with the PageObject in your test.

## A first example
[source can be found here](../master/test/src/test/scala/org/pageobject/examples/readme/Readme.scala)
```scala
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
import org.pageobject.examples.ExamplePageObjectSpec

/**
 * The is a PageObjectSuite for the ReadmePage example.
 */
class Readme extends ExamplePageObjectSpec {

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
```

## Using PageObject
Currently no public release is available. Please check out this repository and run `$ sbt publishLocal` if you want to try it.

## Project Structure
* **core** All Test Runner independent files of PageObject
* **scalatest** Classes needed to use PageObject with ScalaTest 3 
* **test** Unit Tests and examples for PageObject, tested with ScalaTest 3

## Building PageObject
### Prerequisites
The followings are recommended for building PageObject:
(other versions may work but are currently untested)
* JDK 8
* [SBT 0.13.11](http://www.scala-sbt.org/0.13/docs/Getting-Started.html)
* [Scala 2.11.8](http://www.scala-lang.org/documentation/getting-started.html)

### Building and Running Tests
This command will build and run the regular tests:

  `$ sbt test`

If you are using Linux and want to run all Browsers inside of VNC Servers, (recommended for both linux developers and linux build/test servers) use this command:

  `$ RUN_WITH_DRIVERS=org.pageobject.core.driver.vnc.DefaultVncDriverFactoryList sbt test`
