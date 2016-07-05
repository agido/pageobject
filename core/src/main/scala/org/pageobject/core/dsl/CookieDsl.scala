/*
 * Copyright 2001-2016 Artima, Inc.
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
package org.pageobject.core.dsl

import java.util.Date

import org.openqa.selenium.WebDriver

trait CookieDsl {
  protected val add = Cookie.AddCookie
  protected val delete = Cookie.DeleteCookie
  protected val cookies = Cookie.Cookies

  /**
   * Get a saved cookie from web browser, throws TestFailedException if the cookie does not exist.
   *
   * @param name cookie's name
   *
   * @return a WrappedCookie instance
   */
  def cookie(name: String)(implicit driver: WebDriver): Cookie.WrappedCookie = {
    Cookie.getCookie(name)
  }

  /**
   * Add cookie in the web browser.
   *
   * If the cookie's domain name is left blank (default),
   * it is assumed that the cookie is meant for the domain of the current document.
   *
   * @param name cookie's name
   *
   * @param value cookie's value
   *
   * @param path cookie's path
   *
   * @param expiry cookie's expiry data
   *
   * @param domain cookie's domain name
   *
   * @param secure whether this cookie is secured.
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   */
  def addCookie(name: String,
                value: String,
                path: String = "/",
                expiry: Option[Date] = None,
                domain: Option[String] = None,
                secure: Boolean = false)
               (implicit driver: WebDriver): Unit = {
    add cookie(name, value, path, expiry, domain, secure)
  }

  /**
   * Delete cookie with the specified name from web browser,
   * throws TestFailedException if the specified cookie does not exists.
   *
   * @param name cookie's name
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   */
  def deleteCookie(name: String)(implicit driver: WebDriver): Unit = {
    delete cookie name
  }

  /**
   * Delete all cookies in the current domain from web browser.
   *
   * @param driver the <code>WebDriver</code> with which to drive the browser
   */
  def deleteAllCookies()(implicit driver: WebDriver): Unit = {
    delete all cookies
  }
}
