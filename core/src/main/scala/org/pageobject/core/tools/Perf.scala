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
package org.pageobject.core.tools

import scala.util.Try

/**
 * Helper object to check how long a code block needs to execute
 */
object Perf {
  def printlnResult[R](message: Try[R] => String, limit: Long = 0)(block: => R): R = {
    Perf.apply[R]((ms: Long, result: Try[R]) => Predef.println(s"${message(result)} has taken ${ms}ms"), limit) {
      block
    }
  }

  def println[R](message: String, limit: Long = 0)(block: => R): R = {
    Perf.apply((ms: Long, _: Try[R]) => Predef.println(s"$message has taken ${ms}ms"), limit) {
      block
    }
  }

  def apply[R](callback: (Long, Try[R]) => Unit, limit: Long)(block: => R): R = {
    val t0 = System.nanoTime()
    val result: Try[R] = Try {
      block
    }
    val t1 = System.nanoTime()
    val taken = (t1 - t0) / 1000000
    if (taken > limit) {
      callback(taken, result)
    }
    result.get
  }
}
