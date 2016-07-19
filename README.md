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
Currently no stable release of PageObject is available, current version is 0.1.0-SNAPSHOT.

You can find a list of all snapshot versions [here](https://oss.sonatype.org/#nexus-search;quick~org.pageobject).

To use PageObject, add this lines to yours build.sbt:
```
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",

libraryDependencies += "org.pageobject" %% "scalatest" % "0.1.0-SNAPSHOT"
```

Alternative you can checkout this repository and run `$ sbt publishLocal` if you want to modify or try it out.

Because of a bug in ScalaTest (See [#1](https://github.com/agido/pageobject/issues/1)) we need to use a patched version. ```org.pageobject.patch.org.scalatest``` is a current build of ScalaTest 3.0.x, the only difference is that the [PR](https://github.com/scalatest/scalatest/issues/931) to fix this bug was added. We hope that this PR will be merged into ScalaTest but unfortunately it looks like that pull requests are ignored...

## Project Structure
* **core** All Test Runner independent files of PageObject
* **scalatest** Classes needed to use PageObject with ScalaTest 3 
* **test** Unit Tests and examples for PageObject, tested with ScalaTest 3
* **selenium** Selenium Scripts and chrome driver, used for building pageobject
* **vnc** A VNC server and control script reference implementation, used for building pageobject
* **travis** Scripts used by travis to build and upload PageObject to sonatype

## Building PageObject
### Prerequisites
The followings are recommended for building PageObject:
(other versions may work but are currently untested)
* openjdk7 (not supported by scala 2.12.x), openjdk8 or oraclejdk8
* [SBT 0.13.11](http://www.scala-sbt.org/0.13/docs/Getting-Started.html)
* [Scala](http://www.scala-lang.org/documentation/getting-started.html)
 * 2.10.6
 * 2.11.8
 * 2.12.0-M5

Because Selenium requires at least Java 7 it is not possible to use PageObject with Java 6.
maven builds of PageObject for scala 2.10 and 2.11 are using openjdk7 and openjdk8 for scala 2.12.

### Building and Running Tests
This command will build and run the regular tests:

  `$ sbt test`

If you are using Linux and want to run all Browsers inside of VNC Servers, (recommended for both linux developers and linux build/test servers) use this command:

  `$ travis/build.sh`

  which will execute:

  `$ RUN_WITH_DRIVERS=org.pageobject.core.driver.vnc.DefaultVncDriverFactoryList sbt test`

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
