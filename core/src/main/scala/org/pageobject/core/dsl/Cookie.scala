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
import org.openqa.selenium.{Cookie => SeleniumCookie}
import org.pageobject.core.TestHelper

import scala.collection.JavaConverters.asScalaSetConverter

object Cookie {

  /**
   * Wrapper class for a Selenium <code>Cookie</code>.
   *
   * <p>
   * This class provides idiomatic Scala access to the services of an underlying <code>Cookie</code>.
   * You can access the wrapped <code>Cookie</code> via the <code>underlying</code> method.
   * </p>
   */
  final class WrappedCookie(val underlying: SeleniumCookie) {
    /**
     * The domain to which this cookie is visible.
     *
     * <p>
     * This invokes <code>getDomain</code> on the underlying <code>Cookie</code>.
     * </p>
     *
     * @return the domain of this cookie
     */
    def domain: String = underlying.getDomain

    /**
     * The expire date of this cookie.
     *
     * <p>
     * This invokes <code>getExpiry</code> on the underlying <code>Cookie</code>.
     * </p>
     *
     * @return the expire date of this cookie
     */
    def expiry: Option[Date] = Option(underlying.getExpiry)

    /**
     * The name of this cookie.
     *
     * <p>
     * This invokes <code>getName</code> on the underlying <code>Cookie</code>.
     * </p>
     *
     * @return the name of this cookie
     */
    def name: String = underlying.getName

    /**
     * The path of this cookie.
     *
     * <p>
     * This invokes <code>getPath</code> on the underlying <code>Cookie</code>.
     * </p>
     *
     * @return the path of this cookie
     */
    def path: String = underlying.getPath

    /**
     * The value of this cookie.
     *
     * <p>
     * This invokes <code>getValue</code> on the underlying <code>Cookie</code>.
     * </p>
     *
     * @return the value of this cookie
     */
    def value: String = underlying.getValue

    /**
     * Indicates whether the cookie requires a secure connection.
     *
     * <p>
     * This invokes <code>isSecure</code> on the underlying <code>Cookie</code>.
     * </p>
     *
     * @return true if this cookie requires a secure connection.
     */
    def secure: Boolean = underlying.isSecure

    /**
     * Returns the result of invoking <code>equals</code> on the underlying <code>Cookie</code>, passing
     * in the specified <code>other</code> object.
     *
     * <p>
     * Two Selenium <code>Cookie</code>s are considered equal if their name and values are equal.
     * </p>
     *
     * @param other the object with which to compare for equality
     *
     * @return true if the passed object is equal to this one
     */
    override def equals(other: Any): Boolean = underlying == other

    /**
     * Returns the result of invoking <code>hashCode</code> on the underlying <code>Cookie</code>.
     *
     * @return a hash code for this object
     */
    override def hashCode: Int = underlying.hashCode

    /**
     * Returns the result of invoking <code>toString</code> on the underlying <code>Cookie</code>.
     *
     * @return a string representation of this object
     */
    override def toString: String = underlying.toString
  }

  /**
   * This field supports cookie deletion in PageObject DSL.
   *
   * <p>
   * This field enables the following syntax:
   * </p>
   *
   * <pre class="stHighlight">
   * delete all cookies
   * &#94;
   * </pre>
   **/
  sealed trait Cookies

  object Cookies extends Cookies

  private object CookieHelper {
    def getCookie(name: String)(implicit driver: WebDriver): WrappedCookie = {
      driver.manage.getCookies.asScala.find(_.getName == name) match {
        case Some(cookie) =>
          new WrappedCookie(cookie)
        case None =>
          TestHelper.failTest(s"Cookie '$name' not found.")
      }
    }

    def deleteCookie(name: String)(implicit driver: WebDriver): Unit = {
      val cookie = CookieHelper.getCookie(name)
      driver.manage.deleteCookie(cookie.underlying)
    }

    def addCookie(cookie: SeleniumCookie)(implicit driver: WebDriver): Unit = {
      driver.manage.addCookie(cookie)
    }

    def allCookies(implicit driver: WebDriver): Seq[WrappedCookie] = {
      driver.manage.getCookies.asScala.toSeq.map(new WrappedCookie(_))
    }
  }

  /**
   * This object is part of PageObject DSL.
   *
   * <p>
   * This object enables syntax such as the following:
   * </p>
   *
   * <pre class="stHighlight">
   * add cookie("aName", "aValue")
   * &#94;
   * </pre>
   **/
  object AddCookie {
    // Default values determined from
    // http://code.google.com/p/selenium/source/browse/trunk/java/client/src/org/openqa/selenium/Cookie.java
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
    def cookie(name: String,
               value: String,
               path: String = "/",
               expiry: Option[Date] = None,
               domain: Option[String] = None,
               secure: Boolean = false
              )(implicit driver: WebDriver): Unit = {
      CookieHelper.addCookie(new SeleniumCookie(name, value, domain.orNull, path, expiry.orNull, secure))
    }
  }

  /**
   * This object is part of PageObject DSL.
   *
   * <p>
   * This object enables syntax such as the following:
   * </p>
   *
   * <pre class="stHighlight">
   * delete cookie "aName"
   * &#94;
   *
   * delete all cookies
   * &#94;
   * </pre>
   **/
  object DeleteCookie {

    /**
     * Delete cookie with the specified name from web browser,
     * throws TestFailedException if the specified cookie does not exists.
     *
     * @param name cookie's name
     *
     * @param driver the <code>WebDriver</code> with which to drive the browser
     */
    def cookie(name: String)(implicit driver: WebDriver): Unit = {
      CookieHelper.deleteCookie(name)
    }

    /**
     * Delete all cookies in the current domain from web browser.
     *
     * @param driver the <code>WebDriver</code> with which to drive the browser
     */
    def all(cookies: Cookies)(implicit driver: WebDriver): Unit = {
      driver.manage.deleteAllCookies()
    }
  }

  def getCookie(name: String)(implicit driver: WebDriver): WrappedCookie = CookieHelper.getCookie(name)

  def allCookies(implicit driver: WebDriver): Seq[WrappedCookie] = CookieHelper.allCookies
}
