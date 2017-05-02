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
package org.pageobject.core

import org.pageobject.core.dsl.BrowserPageDsl
import org.pageobject.core.page.PageModule
import org.pageobject.scalatest.JettySuite.JettyPage
import org.scalatest.exceptions.TestFailedDueToTimeoutException

class AnimationTest extends TestSpec with BrowserPageDsl {

  case class AnimationTestPage() extends JettyPage {
    val path = "/animation.html"

    object content extends PageModule {
      private def element = $(id("animated"))

      def clickElement(animationDuration: Option[java.lang.Long] = None): Unit = {
        animationDuration.foreach(duration => executeScript(s"animate($duration)"))
        click after animation on element
      }
    }

    override def atChecker() = pageTitle == "Animation"
  }

  it("should be able to click an element without animation") {
    val page = to(AnimationTestPage())
    page.content.clickElement()
  }

  it("should not be able to click an element with short animation") {
    val page = to(AnimationTestPage())
    page.content.clickElement(Some(100L)) // scalastyle:ignore magic.number
  }

  it("should not be able to click an element with medium animation") {
    val page = to(AnimationTestPage())
    page.content.clickElement(Some(1500L)) // scalastyle:ignore magic.number
  }

  it("should not be able to click an element with long animation") {
    val page = to(AnimationTestPage())
    val thrown = intercept[TestFailedDueToTimeoutException] {
      page.content.clickElement(Some(10000L)) // scalastyle:ignore magic.number
    }
    assert(thrown.message.get.startsWith("""The code passed to "waitFor(clickAfterAnimation)" never returned normally. Attempted """))
    assert(thrown.cause.get.getMessage == "animation still in progress")
  }
}
