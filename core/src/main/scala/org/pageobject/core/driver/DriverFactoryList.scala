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
package org.pageobject.core.driver

import org.pageobject.core.tools.Environment
import org.pageobject.core.tools.OS

object DriverFactoryList {
  val useVnc: Boolean = Environment.boolean("PAGEOBJECT_VNC", OS.isLinux)
}

/**
 * A simple scala default implementation for java interface <code>DriverFactories</code>
 *
 * DriverFactoryList will filter out all DriverFactories returning false for compatible or selected
 *
 * @param allDrivers driverFactories that should be used to run the tests.
 */
class DriverFactoryList(allDrivers: DriverFactory*) extends DriverFactories {
  val drivers = allDrivers.filter(factory => factory.compatible && factory.limit.selected && factory.vnc == DriverFactoryList.useVnc)
}
