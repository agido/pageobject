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

object Environment {
  def integer(name: String): Option[Int] = {
    sys.env.get(name).map(Integer.parseInt)
  }

  def integer(name: String, default: Int): Int = {
    sys.env.get(name).fold(default)(Integer.parseInt)
  }

  def parseBoolean(boolean: String): Boolean = boolean.toLowerCase match {
    case "1" | "true" => true
    case "0" | "false" => false
  }

  def boolean(name: String, default: Boolean = false): Boolean = {
    sys.env.get(name).fold(default)(parseBoolean)
  }
}
