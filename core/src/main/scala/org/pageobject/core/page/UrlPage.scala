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
 * Trait for Pages that can be navigated to by using <code>PageBrowser.via()</code>
 * <code>
 *   case class HomePage() extends UrlPage {
 *     val url = "localhost:9000/index.html"
 *   }
 *
 *   via(HomePage())
 * <code>
 */
trait UrlPage {
  /**
   * The URL of the page represented by this page object.
   */
  def url: String
}

/**
 * companion object to create UrlPage instances that are only having an URL.
 */
object UrlPage {
  def apply(url: String): UrlPage = DefaultUrlPage(url)
}

/**
 * Default implementation of UrlPage only contain an URL.
 */
case class DefaultUrlPage(url: String) extends UrlPage
