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

sealed trait LogLevel

object LogLevel {

  case object Trace extends LogLevel

  case object Debug extends LogLevel

  case object Info extends LogLevel

  case object Warn extends LogLevel

  case object Error extends LogLevel

}

trait Logging {
  private lazy val logger = org.slf4j.LoggerFactory.getLogger(getClass.getName)

  def log(level: LogLevel, msg: => String): Unit = level match {
    case LogLevel.Trace => trace(msg)
    case LogLevel.Debug => debug(msg)
    case LogLevel.Info => info(msg)
    case LogLevel.Warn => warn(msg)
    case LogLevel.Error => error(msg)
    case _ =>
  }

  def log(level: LogLevel, msg: => String, throwable: => Throwable): Unit = level match {
    case LogLevel.Trace => trace(msg, throwable)
    case LogLevel.Debug => debug(msg, throwable)
    case LogLevel.Info => info(msg, throwable)
    case LogLevel.Warn => warn(msg, throwable)
    case LogLevel.Error => error(msg, throwable)
    case _ =>
  }

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
