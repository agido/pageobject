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

/**
 * If you use UnexpectedPages.waitPages to implement a waiting page (like a loading screen or "please wait" message)
 * you can implement this trait on the page to change the default wait time the PageBrowser will wait after this page
 * was detected.
 *
 * The default wait time is 500ms.
 */
trait WaitPage extends AtChecker {
  def waitTimeMillis(): Long
}
