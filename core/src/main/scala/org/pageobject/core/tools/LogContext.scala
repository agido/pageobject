package org.pageobject.core.tools

import org.slf4j.MDC

import scala.collection.JavaConversions.mapAsJavaMap
import scala.collection.JavaConversions.mapAsScalaMap

object LogContext {
  val suiteName = "suiteName"
  val testName = "testName"
  val browser = "browser"
  val vnc = "vnc"

  def apply[S](key: String): String = {
    Option(MDC.get(key)).getOrElse("")
  }

  def apply[S](mdcMap: collection.immutable.Map[String, String])(thunk: => S): S = {
    val old = Option(MDC.getCopyOfContextMap)
    MDC.setContextMap(old.map(_.toMap).toSeq.fold(mdcMap)(_ ++ _))
    try {
      thunk
    } finally {
      MDC.setContextMap(old.getOrElse(collection.immutable.Map.empty[String, String]))
    }
  }
}
