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

/**
 * OS detection
 */
object OS {
  private val name = System.getProperty("os.name", "generic").toLowerCase

  val isOSX = name == "mac os x" || name.indexOf("darwin") != -1
  val isWindows = name.startsWith("windows")
  val isLinux = name.indexOf("linux") != -1

  lazy val suffix: String = {
    if (isOSX) {
      "osx"
    } else if (isWindows) {
      "win"
    } else if (isLinux) {
      "linux"
    } else {
      throw new RuntimeException("could not detect OS type!")
    }
  }
}
