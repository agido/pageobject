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

import java.util.concurrent.TimeUnit

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver

import scala.concurrent.duration.FiniteDuration

/**
 * This trait is part of the PageObject DSL.
 *
 * This trait implements javascript commands.
 */
trait ScriptDsl {
  private def withJavascriptExecutor(fn: JavascriptExecutor => Any)(implicit driver: WebDriver): Any = {
    driver match {
      case executor: JavascriptExecutor => fn(executor)
      case _ => throw new UnsupportedOperationException(
        s"Web driver ${driver.getClass.getName} does not support javascript execution.")
    }
  }

  /**
   * Executes JavaScript in the context of the currently selected frame or window.
   * The script fragment provided will be executed as the body of an anonymous function.
   *
   * <p>
   * Within the script, you can use <code>document</code> to refer to the current document.
   * Local variables will not be available once the script has finished executing, but global variables will.
   * </p>
   *
   * <p>
   * To return a value (e.g. if the script contains a return statement), then the following steps will be taken:
   * </p>
   *
   * <ol>
   * <li>For an HTML element, this method returns a WebElement</li>
   * <li>For a decimal, a Double is returned</li>
   * <li>For a non-decimal number, a Long is returned</li>
   * <li>For a boolean, a Boolean is returned</li>
   * <li>For all other cases, a String is returned</li>
   * <li>For an array, return a List<Object> with each object following the rules above. We support nested lists</li>
   * <li>Unless the value is null or there is no return value, in which null is returned</li>
   * </ol>
   *
   * <p>
   * Script arguments must be a number, boolean, String, WebElement, or a List of any combination of these.
   * An exception will be thrown if the arguments do not meet these criteria. The arguments will be made available
   * to the JavaScript via the "arguments" variable. (Note that although this behavior is specified by
   * <a href="http://selenium.googlecode.com/git/docs/api/java/org/openqa/selenium/JavascriptExecutor.html">
   * Selenium's JavascriptExecutor Javadoc</a>, it may still be possible for the underlying
   * <code>JavascriptExecutor</code> implementation to return an objects of other types. For example,
   * <code>HtmlUnit</code> has been observed to return a <code>java.util.Map</code> for a Javascript object.)
   * </p>
   *
   * @param script the JavaScript to execute
   *
   * @param args the arguments to the script, may be empty
   *
   * @return One of Boolean, Long, String, List or WebElement. Or null (following
   *         <a href="http://selenium.googlecode.com/git/docs/api/java/org/openqa/selenium/JavascriptExecutor.html">
   *         Selenium's JavascriptExecutor Javadoc</a>)
   */
  protected def executeScript[T](script: String, args: AnyRef*)(implicit driver: WebDriver): Any = {
    withJavascriptExecutor(_.executeScript(script, args.toArray: _*))
  }

  /**
   * Executes an asynchronous piece of JavaScript in the context of the currently selected frame or window.
   *
   * Unlike executing synchronous JavaScript, scripts executed with this method must explicitly signal they are
   * finished by invoking the provided callback. This callback is always injected into the executed function
   * as the last argument.
   *
   * <p>
   * The first argument passed to the callback function will be used as the script's result.
   * This value will be handled as follows:
   * </p>
   *
   * <ol>
   * <li>For an HTML element, this method returns a WebElement</li>
   * <li>For a number, a Long is returned</li>
   * <li>For a boolean, a Boolean is returned</li>
   * <li>For all other cases, a String is returned</li>
   * <li>For an array, return a List<Object> with each object following the rules above. We support nested lists</li>
   * <li>Unless the value is null or there is no return value, in which null is returned</li>
   * </ol>
   *
   * <p>
   * Script arguments must be a number, boolean, String, WebElement, or a List of any combination of these.
   * An exception will be thrown if the arguments do not meet these criteria.
   * The arguments will be made available to the JavaScript via the "arguments" variable. (Note that although this
   * behavior is specified by
   * <a href="http://selenium.googlecode.com/git/docs/api/java/org/openqa/selenium/JavascriptExecutor.html">
   * Selenium's JavascriptExecutor Javadoc</a>, it may still be possible for the underlying
   * <code>JavascriptExecutor</code> implementation to return an objects of other types. For example,
   * <code>HtmlUnit</code> has been observed to return a <code>java.util.Map</code> for a Javascript object.)
   * </p>
   *
   * @param script the JavaScript to execute
   *
   * @param args the arguments to the script, may be empty
   *
   * @return One of Boolean, Long, String, List, WebElement, or null (following
   *         <a href="http://selenium.googlecode.com/git/docs/api/java/org/openqa/selenium/JavascriptExecutor.html">
   *         Selenium's JavascriptExecutor Javadoc</a>)
   */
  protected def executeAsyncScript(script: String, args: AnyRef*)(implicit driver: WebDriver): Any = {
    withJavascriptExecutor(executor => executor.executeAsyncScript(script, args.toArray: _*))
  }

  /**
   * Sets the amount of time to wait for an asynchronous script to finish execution before throwing an exception.
   *
   * @param timeout the amount of time to wait for an asynchronous script to finish execution before throwing exception
   */
  protected def setScriptTimeout(timeout: FiniteDuration)(implicit driver: WebDriver): Unit = {
    driver.manage().timeouts().setScriptTimeout(timeout.toNanos, TimeUnit.NANOSECONDS)
  }
}
