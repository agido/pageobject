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
package org.pageobject.examples.wikipedia

import org.pageobject.core.api.ImplicitCssSelectorQuery
import org.pageobject.core.page.DomainPage
import org.pageobject.core.page.KeyboardModule
import org.pageobject.core.page.PageObject

abstract class WikipediaDomainPage(subdomain: String) extends PageObject with DomainPage with ImplicitCssSelectorQuery {
  val domain = s"https://$subdomain.wikipedia.org"

  object keyboard extends KeyboardModule

}
