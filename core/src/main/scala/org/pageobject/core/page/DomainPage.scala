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
 * Trait to separate the domain and the path of the page to test.
 *
 * You can write your own implementation of <code>domain</code> and
 * query e.g. the system environment to decide if a test server url or
 * a local development url should be returned.
 *
 * Example:
 * <code>
 *   trait GoogleDomainPage extends DomainPage {
 *     val domain = sys.env.getOrElse("DEV_SERVER_PREFIX", "https://www.google.com")
 *   }
 * </code>
 */
trait DomainPage extends UrlPage {
  /**
   * The "domain" of the page to test.
   *
   * You need to specify a full qualified url without path here.
   *
   * Example:
   * <code>
   *   val domain = "https://my.testserver.com:12345"
   * </code>
   *
   * @note the string should not end with slash
   */
  def domain: String

  /**
   * The path of the page represented by this page object. That is the path from the domain to the page.
   *
   * Example:
   * <code>
   *   val url = "/index.html"
   * </code>
   *
   * @note the string should start with slash
   */
  def path: String

  final override def url = domain + path
}
