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
 * Query the system environment for limit values
 */
object Limit {
  def getLimitName(limit: String): String = s"${limit.toUpperCase}_LIMIT"

  def getRawLimit(limit: String): Int = {
    sys.env.get(limit)
      .map(value => Try[Int](Integer.parseInt(value)))
      .flatMap(_.toOption)
      .getOrElse(1)
  }

  def getLimit(browser: String): Int = {
    getRawLimit(getLimitName(browser))
  }
}
