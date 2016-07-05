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
package org.pageobject.core.dsl

import org.openqa.selenium.StaleElementReferenceException

import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.util.control.NonFatal

object RetryHelper {
  val defaultRetryCount = 3

  def retryOnStaleElementReferenceException(th: Throwable): Boolean = th.isInstanceOf[StaleElementReferenceException]

  def retryOnClickFailed(th: Throwable): Boolean = {
    th.getMessage.startsWith("unknown error: Element is not clickable at point")
  }

  def join(functions: ((Throwable) => Boolean)*): (Throwable) => Boolean = {
    th => functions.exists(_ (th))
  }

  def apply[T](retryOn: Throwable => Boolean, retryCount: Int = defaultRetryCount, recover: () => Unit = () => ())(action: => T): T = {
    def doit(): Either[Try[T], Failure[T]] = {
      try Left(Success(action)) catch {
        case e: Throwable if retryOn(e) => Left(Failure(e))
        case NonFatal(e) => Right(Failure(e))
      }
    }

    List.fill(retryCount)(() => doit()).reduce((first, retry) => first() match {
      // action was successfully
      case result@Left(Success(_)) =>
        () => result

      // expected exception, retry
      case Left(Failure(_)) =>
        recover()
        retry

      // some other exception
      case exception@Right(_) =>
        () => exception
    })() match {
      case Left(result) => result.get
      case Right(result) => result.get
    }
  }
}
