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

import scala.concurrent.duration
import scala.language.implicitConversions

// TODO check against scala 2.12
trait DurationDsl {
  type Duration = scala.concurrent.duration.Duration
  type FiniteDuration = scala.concurrent.duration.FiniteDuration

  protected final val span = duration.span
  protected final val fromNow = duration.fromNow
  protected type TimeUnit = duration.TimeUnit
  protected final val DAYS = duration.DAYS
  protected final val HOURS = duration.HOURS
  protected final val MICROSECONDS = duration.MICROSECONDS
  protected final val MILLISECONDS = duration.MILLISECONDS
  protected final val MINUTES = duration.MINUTES
  protected final val NANOSECONDS = duration.NANOSECONDS
  protected final val SECONDS = duration.SECONDS

  protected implicit def pairIntToDuration(p: (Int, TimeUnit)): Duration = duration.pairIntToDuration(p)

  protected implicit def pairLongToDuration(p: (Long, TimeUnit)): FiniteDuration = duration.pairLongToDuration(p)

  protected implicit def durationToPair(d: Duration): (Long, TimeUnit) = duration.durationToPair(d)

  protected implicit def durationInt(n: Int): duration.DurationConversions = duration.DurationInt(n)

  protected implicit def durationLong(n: Long): duration.DurationConversions = duration.DurationLong(n)

  protected implicit def durationDouble(n: Double): duration.DurationConversions = duration.DurationDouble(n)

  protected implicit def intMult(i: Int): duration.IntMult = duration.IntMult(i)

  protected implicit def longMult(l: Long): duration.LongMult = duration.LongMult(l)

  protected implicit def doubleMult(d: Double): duration.DoubleMult = duration.DoubleMult(d)
}
