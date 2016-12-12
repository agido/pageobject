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
package org.pageobject.core.driver;

import scala.collection.Seq;

/**
 * Classes implementing this interface can be used as an argument for {@code &#064;RunWithDrivers}.
 *
 * A default scala implementation of this interface is provided here: {@code DriverFactoryList}.
 */
public interface DriverFactories {
    /**
     * For each returned driver factory a test instance is started and configured using this factory.
     *
     * @return a list of driver factories.
     */
    Seq<DriverFactory> drivers();
}
