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
package org.pageobject.core

import org.openqa.selenium.InvalidSelectorException
import org.openqa.selenium.NoSuchFrameException
import org.openqa.selenium.NoSuchSessionException
import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.SessionNotCreatedException
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.UnableToSetCookieException
import org.openqa.selenium.UnhandledAlertException
import org.openqa.selenium.UnsupportedCommandException
import org.openqa.selenium.firefox.UnableToCreateProfileException
import org.openqa.selenium.remote.ErrorHandler.UnknownServerException
import org.openqa.selenium.remote.ScreenshotException
import org.openqa.selenium.remote.UnreachableBrowserException
import org.openqa.selenium.safari.ConnectionClosedException

object SeleniumException {
  def apply(th: Throwable): Boolean = th match {
    case _: InvalidSelectorException |
         _: NoSuchFrameException |
         _: NoSuchSessionException |
         _: NoSuchWindowException |
         _: SessionNotCreatedException |
         _: TimeoutException |
         _: UnableToSetCookieException |
         _: UnhandledAlertException |
         _: UnsupportedCommandException |
         _: UnableToCreateProfileException |
         _: UnknownServerException |
         _: ScreenshotException |
         _: UnreachableBrowserException |
         _: ConnectionClosedException => true
    case _ => false
  }

  def unapply(t: Throwable): Option[Throwable] = if (apply(t)) Some(t) else None
}
