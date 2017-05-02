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
package org.pageobject.core

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

import org.pageobject.core.WaitFor.PatienceConfig
import org.pageobject.core.WaitFor.PatienceMap
import org.pageobject.core.WaitFor.WaitForHolder
import org.pageobject.core.dsl.DurationDsl
import org.pageobject.core.tools.DynamicOptionVariable
import org.pageobject.core.tools.Logging

import scala.annotation.tailrec
import scala.util.control.NonFatal

/**
 * WaitFor provides a way to configure the patience for different actions.
 *
 * In opposide to ScalaTest's Eventually, waitFor will abort when a WebDriverException was detected.
 * You can also not scale all timeouts like in AbstractPatienceConfiguration.
 *
 * Example:
 * <code>
 *   object PageBrowser extends WaitFor {
 *     // timeout how long to wait for the expected page
 *     object At extends PatienceConfig(30.seconds)
 *   }
 * </code>
 *
 * Example:
 * <code>
 *   waitFor(PageBrowser.At) {
 *     assert(...) // will try up to 30 seconds until the test will be aborted
 *   }
 * </code>
 *
 * If you want to override this value for a test, use withPatience:
 * <code>
 *   withPatience(PageBrowser.At -> 1.second) {
 *     // the at checker will now already be aborted after one second...
 *   }
 * </code>
 *
 */
trait WaitFor extends DurationDsl with Logging {
  private def tryFunction[T](fun: => T): Either[Throwable, T] = {
    try {
      // Right return a success value
      Right(fun)
    } catch {
      case SeleniumException(ex) => throw ex
      case NonFatal(th) =>
        if (TestHelper.isTestAbortError(th)) {
          // rethrowing will break out of waitFor and throw the given exception
          throw th
        } else {
          // Left will tell waitFor to retry
          Left(th)
        }
    }
  }

  protected def waitFor[T](description: String, timeout: FiniteDuration, interval: FiniteDuration)(fun: => T): T = {
    waitFor(description, WaitFor.PatienceConfig(timeout, interval))(fun)
  }

  protected def withPatience[T](config: (PatienceConfig, PatienceConfig)*)(fun: => T): T = {
    withPatience(config.toMap) {
      fun
    }
  }

  protected def waitFor[T](description: String, config: PatienceConfig)(fun: => T): T = {
    val mapped = WaitForHolder.option.flatMap(_.get(config)).getOrElse(config)
    val timeout = mapped.timeout
    val interval = mapped.interval
    val startNanos = System.nanoTime

    @tailrec
    def tryIt(attempt: Int): T = tryFunction(fun) match {
      case Right(result) => result
      case Left(e) =>
        val duration = System.nanoTime - startNanos
        if (duration < timeout.toNanos) {
          // For first interval, we wake up every 1/10 of the interval.  This is mainly for optimization purpose.
          if (duration < interval.toMillis) {
            Thread.sleep(interval.toMillis / 10)
          } else {
            Thread.sleep(interval.toMillis)
          }
          debug(s"retrying $description (caused by ${e.getMessage})")
          tryIt(attempt + 1)
        } else {
          val ms = TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS)
          val message = s"""The code passed to "waitFor($description)" never returned normally. Attempted $attempt times over ${ms}ms."""
          val failure = Option(e).map(_.getMessage).fold("")(msg => s"\nLast failure message: $msg")
          TestHelper.timeoutTest(message + failure, timeout)
        }
    }

    tryIt(1)
  }

  protected def withPatience[T](map: PatienceMap)(fun: => T): T = {
    val merged = WaitForHolder.option.fold(map)(_ ++ map)
    WaitForHolder.withValue(merged) {
      fun
    }
  }
}

/**
 * Companion object WaitFor, only for internal usage
 */
object WaitFor extends DurationDsl {
  private val counter = new AtomicLong

  private def nextId = counter.getAndIncrement()

  private val waitForDelegate = new WaitFor {}

  /**
   * Because each PatienceConfig should be unique, which is required to override values using
   * <code>WaitFor.withPatience()</code>, each PatienceConfig has an uniq field just counted up for every instance.
   */
  case class PatienceConfig(timeout: FiniteDuration, interval: FiniteDuration, uniq: Long = nextId) {
    override def toString: String = {
      s"${getClass.getName}($timeout, $interval)"
    }
  }

  type PatienceMap = Map[PatienceConfig, PatienceConfig]

  private object WaitForHolder extends DynamicOptionVariable[PatienceMap]()

  def withPatience[T](map: PatienceMap)(fun: => T): T = {
    waitForDelegate.withPatience(map)(fun)
  }
}
