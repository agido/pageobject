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

import org.pageobject.core.TestHelper

/**
 * Because the scala test assert macros, when using DiagrammedAssertions,
 * generate code that don't throw AssertionError's,
 * this object is used to detect the cases like this.
 */
object AssertionFailed {
  def apply(t: Throwable): Boolean = t match {
    case _: AssertionError => true
    case th: Throwable => TestHelper.isAssertionError(th)
  }

  def unapply(t: Throwable): Option[Throwable] = if (apply(t)) Some(t) else None
}
