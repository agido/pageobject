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
package org.pageobject.core.page

import org.pageobject.core.BrowserErrorPage
import org.pageobject.core.tools.DynamicOptionVariable

import scala.collection.Seq

/**
 * UnexpectedPages will be checked by <code>PageBrowser.at()</code> to prevent
 * running in a timeout when waiting for an expected page.
 *
 * Unexpected PageObjects have normally no URL, are not implementing UrlPage, but this is not required.
 *
 * Examples are BrowserErrorPage, "404 File Not Found" or "5xx Server Error" Pages.
 *
 * Also Error Pages delivered by Load Balancers (for example AWS Elastic Load Balancer or CloudFlare)
 * can be checked here.
 *
 * There are two groups of unexpected Pages, the first group will cancel the test, the other
 * group will fail the test.
 *
 * "File not Found" or "Internal Server Error" should cause a test to fail, but "connection refused"
 * should cancel the test because test server is not reachable. This is typically an environment problem,
 * normally not a problem with the test itself.
 */
object UnexpectedPages {
  type AtCheckerProvider = () => AtChecker

  val defaultCancelTestPages: Seq[AtCheckerProvider] = Seq(() => BrowserErrorPage())
  val defaultFailTestPages: Seq[AtCheckerProvider] = Seq()
  val defaultWaitPages: Seq[AtCheckerProvider] = Seq()
}

/**
 * Because a PageObject will be chained to an PageBrowser, every PageBrowser needs an own instance of the error page.
 *
 * Because of this there are two traits, UnexpectedPages contain PageObjects and UnexpectedPagesFactory containg
 * functions returning PageObjects
 *
 * @tparam T the type of pages, either "AtChecker" or "() => AtChecker"
 */
trait BaseUnexpectedPages[T] {
  /**
   * A list of pages that will trigger a test failure when one of this pages was found while calling at()
   *
   * @return a list of pages
   */
  def failTestPages: Seq[T]

  /**
   * A list of pages that will trigger a test to be ignored when one of this pages was found while calling at()
   *
   * @return a list of pages
   */
  def cancelTestPages: Seq[T]

  /**
   * A list of pages that will force the test to wait until the page was disappeared or a timeout occured.
   *
   * @return a list of pages
   */
  def waitPages: Seq[T]
}

/**
 * This type is used by <code>UnexpectedPagesFactory.createUnexpectedPages()</code>
 **/
trait UnexpectedPages extends BaseUnexpectedPages[AtChecker]

/**
 * This type is used by to represent a list of functions returning unexpected pages.
 **/
trait UnexpectedPagesFactory extends BaseUnexpectedPages[UnexpectedPages.AtCheckerProvider]

/**
 * This class is used to manage the list of UnexpectedPages.
 *
 * Example:
 * <code>
 * it("should detect browsers connection refused page")
 *   via(UrlPage("http://localhost:65534/"))
 *   UnexpectedPagesFactory.withUnexpectedPages(EmptyUnexpectedPagesFactory()) {
 *     at(BrowserErrorPage())
 *   }
 * }
 * </code>
 *
 * You can configure the unexpected pages using a custom DriverFactoryList:
 * <code>
 * class MyDriverFactoryList extends DefaultDriverFactoryList with UnexpectedPagesFactoryProvider {
 *   override val unexpectedPagesFactory = UnexpectedPagesFactory(cancelTestPages = Seq(() => MyErrorPage()))
 * }
 * </code>
 */
object UnexpectedPagesFactory {
  def withUnexpectedPages[R](provider: UnexpectedPagesFactoryProvider)(fn: => R): R = {
    UnexpectedPagesFactoryHolder.withValue(provider.unexpectedPagesFactory) {
      fn
    }
  }

  def withUnexpectedPages[R](factory: UnexpectedPagesFactory)(fn: => R): R = {
    UnexpectedPagesFactoryHolder.withValue(factory) {
      fn
    }
  }

  def withMaybeUnexpectedPages[R](unexpectedPagesFactory: AnyRef)(fn: => R): R = unexpectedPagesFactory match {
    case provider: UnexpectedPagesFactoryProvider => withUnexpectedPages(provider)(fn)
    case factory: UnexpectedPagesFactory => withUnexpectedPages(factory)(fn)
    case _ => fn
  }

  def createUnexpectedPages(factory: UnexpectedPagesFactory = UnexpectedPagesFactoryHolder.value): UnexpectedPages = {
    DefaultUnexpectedPages(factory.cancelTestPages.map(_ ()), factory.failTestPages.map(_ ()), factory.waitPages.map(_ ()))
  }

  def apply(cancelTestPages: Seq[UnexpectedPages.AtCheckerProvider] = UnexpectedPages.defaultCancelTestPages,
            failTestPages: Seq[UnexpectedPages.AtCheckerProvider] = UnexpectedPages.defaultFailTestPages,
            waitPages: Seq[UnexpectedPages.AtCheckerProvider] = UnexpectedPages.defaultWaitPages
           ): UnexpectedPagesFactory = {
    DefaultUnexpectedPagesFactory(cancelTestPages, failTestPages, waitPages)
  }

  private object UnexpectedPagesFactoryHolder
    extends DynamicOptionVariable[UnexpectedPagesFactory](DefaultUnexpectedPagesFactory())

}

/**
 * Default implementation of UnexpectedPages
 *
 * @param cancelTestPages list of pages that should cancel a test when detected
 *
 * @param failTestPages list of pages that should fail a test when detected
 */
case class DefaultUnexpectedPages(cancelTestPages: Seq[AtChecker] = UnexpectedPages.defaultCancelTestPages.map(_ ()),
                                  failTestPages: Seq[AtChecker] = UnexpectedPages.defaultFailTestPages.map(_ ()),
                                  waitPages: Seq[AtChecker] = UnexpectedPages.defaultWaitPages.map(_ ()))
  extends UnexpectedPages

/**
 * Default implementation of UnexpectedPages with empty lists for cancelTestPages and failTestPages
 */
case class EmptyUnexpectedPagesFactory() extends UnexpectedPagesFactory {
  val cancelTestPages = Seq()
  val failTestPages = Seq()
  val waitPages = Seq()
}

/**
 * Default UnexpectedPagesFactory implementation
 */
case class DefaultUnexpectedPagesFactory(cancelTestPages: Seq[UnexpectedPages.AtCheckerProvider] =
                                         UnexpectedPages.defaultCancelTestPages,
                                         failTestPages: Seq[UnexpectedPages.AtCheckerProvider] =
                                         UnexpectedPages.defaultFailTestPages,
                                         waitPages: Seq[UnexpectedPages.AtCheckerProvider] =
                                         UnexpectedPages.defaultWaitPages)
  extends UnexpectedPagesFactory

/**
 * Use this trait if you want to provide a UnexpectedPagesFactory.
 */
trait UnexpectedPagesFactoryProvider {
  /**
   * A configuration of unexpected pages.
   *
   * @return a object providing unexpected pages
   */
  def unexpectedPagesFactory: UnexpectedPagesFactory
}
