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
package org.pageobject.core.tools

trait Logging {
  private lazy val logger = org.slf4j.LoggerFactory.getLogger(getClass.getName)

  def trace(msg: => String): Unit = {
    if (logger.isTraceEnabled) {
      logger.trace(msg)
    }
  }

  def trace(msg: => String, throwable: => Throwable): Unit = {
    if (logger.isTraceEnabled) {
      logger.trace(msg, throwable)
    }
  }

  def debug(msg: => String): Unit = {
    if (logger.isDebugEnabled) {
      logger.debug(msg)
    }
  }

  def debug(msg: => String, throwable: => Throwable): Unit = {
    if (logger.isDebugEnabled) {
      logger.debug(msg, throwable)
    }
  }

  def info(msg: => String): Unit = {
    if (logger.isInfoEnabled) {
      logger.info(msg)
    }
  }

  def info(msg: => String, throwable: => Throwable): Unit = {
    if (logger.isInfoEnabled) {
      logger.info(msg, throwable)
    }
  }

  def warn(msg: => String): Unit = {
    if (logger.isWarnEnabled) {
      logger.warn(msg)
    }
  }

  def warn(msg: => String, throwable: => Throwable): Unit = {
    if (logger.isWarnEnabled) {
      logger.warn(msg, throwable)
    }
  }

  def error(msg: => String): Unit = {
    if (logger.isErrorEnabled) {
      logger.error(msg)
    }
  }

  def error(msg: => String, throwable: => Throwable): Unit = {
    if (logger.isErrorEnabled) {
      logger.error(msg, throwable)
    }
  }
}
