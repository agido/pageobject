[ ![Build Status] [travis-image] ] [travis]
[ ![License] [license-image] ] [license]
[ ![Dependencies] [dependencies-image] ] [dependencies]
[ ![Coverage Status] [coverage-image] ] [coverage]

# PageObject
A Page Object Pattern implementation written in Scala.

PageObject is a free, open-source ([Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt)) extension for test runners implementing the Page Object Pattern.
Currently only ScalaTest 3 is supported, but other testing frameworks should be easy to add.

Official Website: https://www.pageobject.org/

PageObject was initially based on [ScalaTest's Selenium Support](http://www.scalatest.org/user_guide/using_selenium) and has adopted some features of [geb](http://www.gebish.org/).

## Changelog
You can find the Changelog [here](CHANGELOG.md)

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

## ScalaTest Bug
Because of a bug in ScalaTest (See [#1](https://github.com/agido/pageobject/issues/1)) we need to use a patched version. ```org.pageobject.patch.org.scalatest``` is a current build of ScalaTest 3.0.x, the only difference is that the [PR](https://github.com/scalatest/scalatest/issues/931) to fix this bug was added. We hope that this PR will be merged into ScalaTest but unfortunately it looks like that pull requests are ignored...

## Project Structure
* **core** All Test Runner independent files of PageObject
* **examples** examples how to use PageObject with ScalaTest 3
* **howto/maven** example project how to use pageobject with maven
* **maven** maven build configuration, used for internal testing only
* **project** sbt build configuration used to build pageobject
* **scalatest** Classes needed to use PageObject with ScalaTest 3 
* **selenium** Selenium Scripts and chrome driver, used for building pageobject
* **jetty** base project for unit tests and examples
* **test/maven** internal maven project to test maven build
* **test** project used to test pageobject against selenium 3
* **travis** Scripts used by travis to build and upload PageObject to sonatype
* **vnc** A VNC server and control script reference implementation, used for building pageobject

## Building PageObject
### Prerequisites
The followings are recommended for building PageObject:
(other versions may work but are currently untested)
* openjdk8 or oraclejdk8
* [SBT 0.13.x](http://www.scala-sbt.org/0.13/docs/Getting-Started.html)
* [Scala](http://www.scala-lang.org/documentation/getting-started.html)
 * 2.11.12
 * 2.12.4

### Building and Running Tests
This command will build and run the regular tests:

  `$ sbt test`

If you are using Linux and want to run all Browsers inside of VNC Servers, (recommended for both linux developers and linux build/test servers) use this command:

  `$ travis/build.sh`

Before first run you need to download VNC and selenium files:

  `$ travis/setup.sh`

[travis]: https://travis-ci.org/agido/pageobject
[travis-image]: https://travis-ci.org/agido/pageobject.svg?branch=master
[license-image]: http://img.shields.io/badge/license-Apache--2-brightgreen.svg?style=flat
[license]: http://www.apache.org/licenses/LICENSE-2.0
[dependencies]: https://app.updateimpact.com/latest/755117671372165120/pageobject
[dependencies-image]: https://app.updateimpact.com/badge/755117671372165120/pageobject.svg?config=compile
[coverage]: https://coveralls.io/github/agido/pageobject?branch=master
[coverage-image]: https://coveralls.io/repos/github/agido/pageobject/badge.svg?branch=master
