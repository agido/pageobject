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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to provide {@code DriverFactories} to {@code DriverLauncher}.
 *
 * It is recommended to create a common base class used by all of your tests.
 * This class should then be annotated with &#064;RunWithDriver
 *
 * Because only constant arguments are allowed as annotation arguments,
 * we can't just pass the DriverFactory in.
 *
 * You need to create a class with default constuctor,
 * a reference to this class can then be passed to {@code &#064;RunWithDrivers}
 *
 * {@code
 *   class ExampleDriverFactory extends DriverFactoryList(ChromeDriverFactory, FirefoxDriverFactory)
 *
 *   &#064;RunWithDriver(classOf[ExampleDriverFactory])
 *   class ExampleSpec extends FunSpec with DriverLauncher {
 *     // ...
 *   }
 * }
 *
 * When no @RunWithDrivers annotation was found, the default implementation {@code DefaultDriverFactory}
 * will be used. You can configure the default implementation using the environment variable "RUN_WITH_DRIVERS".
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface RunWithDrivers {

    /**
     * newInstance() will be called on the return value of this function.
     * drivers() will then be called on the new instance.
     *
     * Optinally the class returned by value() can implement
     * the trait UnexpectedPagesFactoryProvider to customize the unexpected pages.
     *
     * @return A class extending DriverFactories
     */
    Class<? extends DriverFactories> value();
}
