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

import org.scalatest.Status
import org.scalatest.exceptions.NotAllowedException
import org.scalatest.exceptions.StackDepthException
import org.scalatest.exceptions.TestCanceledException
import org.scalatest.exceptions.TestFailedDueToTimeoutException
import org.scalatest.exceptions.TestFailedException
import org.scalatest.time.Span.convertDurationToSpan

import scala.concurrent.duration.FiniteDuration

class TestHelperImpl extends TestHelper {
  def failTest(message: String): Nothing = {
    throw new TestFailedException(message, 1)
  }

  def failTest(throwable: Throwable): Nothing = {
    throw new TestFailedException(throwable, 1)
  }

  def cancelTest(message: String): Nothing = {
    throw new TestCanceledException(message, 1)
  }

  def cancelTest(throwable: Throwable): Nothing = {
    throw new TestFailedException(throwable, 1)
  }

  def timeoutTest(message: String, timeout: FiniteDuration): Nothing = {
    throw new TestFailedDueToTimeoutException((_: StackDepthException) => Some(message), None,
      Right((_: StackDepthException) => 1), None, timeout)
  }

  def notAllowed(message: String): Nothing = {
    throw new NotAllowedException(message, 1)
  }

  def isFailedResult[T](result: T): Boolean = result match {
    case status: Status => !status.succeeds()
    case _ => false
  }

  def isAssertionError(th: Throwable): Boolean = th match {
    case tfe: TestFailedException => tfe.getStackTrace()(0).getMethodName.startsWith("newAssertionFailedException")
    case _ => false
  }

  def isTestAbortError(th: Throwable): Boolean = th match {
    case ae: AssertionError if ae.getStackTrace.head.getClassName.startsWith("org.easymock") => true
    case th: Throwable if isAssertionError(th) => false
    case tce: TestCanceledException => true
    case tfe: TestFailedException => true
    case _ => false
  }
}
