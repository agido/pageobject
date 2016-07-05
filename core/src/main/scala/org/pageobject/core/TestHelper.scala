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

import scala.concurrent.duration.FiniteDuration

trait TestHelper {
  def failTest[T](message: String): T

  def failTest[T](throwable: Throwable): T

  def cancelTest[T](message: String): T

  def cancelTest[T](throwable: Throwable): T

  def timeoutTest[T](message: String, timeout: FiniteDuration): T

  def notAllowed[T](message: String): T

  def isFailedResult[T](result: T): Boolean

  def isAssertionError(th: Throwable): Boolean

  def isTestAbortError(th: Throwable): Boolean
}

object TestHelper extends TestHelper {

  private val delegate = TestHelper.getClass.getClassLoader
    .loadClass("org.pageobject.core.TestHelperImpl").newInstance().asInstanceOf[TestHelper]

  def failTest[T](message: String): T = delegate.failTest(message)

  def failTest[T](throwable: Throwable): T = delegate.failTest(throwable)

  def cancelTest[T](message: String): T = delegate.cancelTest(message)

  def cancelTest[T](throwable: Throwable): T = delegate.cancelTest(throwable)

  def timeoutTest[T](message: String, timeout: FiniteDuration): T = delegate.timeoutTest(message, timeout)

  def notAllowed[T](message: String): T = delegate.notAllowed(message)

  def isFailedResult[T](result: T): Boolean = delegate.isFailedResult(result)

  def isAssertionError(th: Throwable): Boolean = delegate.isAssertionError(th)

  def isTestAbortError(th: Throwable): Boolean = delegate.isTestAbortError(th)

}
