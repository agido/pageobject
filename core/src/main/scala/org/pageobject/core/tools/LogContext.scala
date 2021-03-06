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

import org.slf4j.MDC

import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.collection.JavaConverters.mapAsScalaMapConverter

object LogContext {
  val activePage = "activePage"
  val suiteName = "suiteName"
  val testName = "testName"
  val browser = "browser"
  val vnc = "vnc"

  def set(key: String, value: Option[String]): Unit = value match {
    case Some(v) => MDC.put(key, v)
    case None => MDC.remove(key)
  }

  def apply[S](key: String): String = {
    Option(MDC.get(key)).getOrElse("")
  }

  def apply[S](mdcMap: collection.immutable.Map[String, String])(thunk: => S): S = {
    val old = Option(MDC.getCopyOfContextMap)
    MDC.setContextMap(old.map(_.asScala.toMap).toSeq.fold(mdcMap)(_ ++ _).asJava)
    try {
      thunk
    } finally {
      MDC.setContextMap(old.getOrElse(collection.immutable.Map.empty[String, String].asJava))
    }
  }
}
