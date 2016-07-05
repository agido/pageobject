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
package org.pageobject.scalatest

import org.pageobject.core.tools.Limit
import org.scalatest.Suite

/**
 * Support for running multiple browsers in parallel.
 *
 * If a Suite implements this trait
 * you can configure how many instances of a given browser will be run at the same time.
 *
 * Overall test limit:
 * Use TEST_LIMIT=-1 to allow unlimited parallel test runs
 * The default TEST_LIMIT is 1, use TEST_LIMIT=4 to allow 4 tests to be executed at the same time.
 *
 * Browser test limit:
 * When not configured, a limit of 1 is assumed.
 * Use 0 to disable a Browser.
 * Use -1 to allow an unlimited count for the given Browser type.
 *
 * Example:
 * FIREFOX_LIMIT=0
 * ->Do not start Firefox tests
 *
 * Example:
 * FIREFOX_LIMIT=3
 * CHROME_LIMIT=5
 * TEST_LIMIT=5
 * -> Start up to 5 tests, but no more then 3 Firefox Tests at the same time.
 *
 * Only Browsers that are supperted for the platform where the tests are executed will be started.
 * E.g. you don't need to disable Internet Explorer or Safari on Linux
 */
trait BrowserLimitSuite extends ConfigureableParallelTestLimit {
  this: Suite =>

  protected override def parallelTestLimitEnvName =
    Seq(Limit.getLimitName(suiteName), "BROWSER_LIMIT") ++ super.parallelTestLimitEnvName
}
