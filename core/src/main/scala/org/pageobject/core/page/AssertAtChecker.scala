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

import org.pageobject.core.tools.AssertionFailed

/**
 * If you want to use geb-like at checker, use this trait.
 *
 * Example:
 * <code>
 *   def at(): Unit = {
 *     assert(pageTitle == "Expected Page Title")
 *   }
 * </code>
 */
trait AssertAtChecker extends AtChecker {
  def at(): Unit

  final def atChecker(): Boolean = {
    try {
      at()
      true
    } catch {
      case AssertionFailed(_) => false
    }
  }
}
