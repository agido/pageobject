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
package org.pageobject.core.driver

import java.util

import org.openqa.selenium.Capabilities
import org.openqa.selenium.remote.CommandExecutor
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.RemoteWebElement
import org.openqa.selenium.remote.Response
import org.pageobject.core.tools.Perf

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.collection.immutable
import scala.collection.mutable
import scala.util.Failure
import scala.util.Success
import scala.util.Try

/**
 * Heper object containg a mapping of fields to dump when a RemoteWebDriver command was executed.
 **/
private object TracedRemoteWebDriver {
  val commandArguments: immutable.Map[String, Seq[String]] = immutable.Map(
    "newSession" -> Seq(),
    "setWindowPosition" -> Seq("x", "y"),
    "setWindowSize" -> Seq("width", "height"),
    "get" -> Seq("url"),
    "findElements" -> Seq("value", "using"),
    "findChildElements" -> Seq("id", "value", "using"),
    "isElementDisplayed" -> Seq("id"),
    "isElementSelected" -> Seq("id"),
    "getElementText" -> Seq("id"),
    "getElementTagName" -> Seq("id"),
    "clickElement" -> Seq("id"),
    "getElementAttribute" -> Seq("id", "name"),
    "sendKeysToElement" -> Seq("id", "value"),
    "clearElement" -> Seq("id"),
    "getTitle" -> Seq(),
    "getCurrentUrl" -> Seq(),
    "close" -> Seq()
  )
}

/**
 * This class extends the Selenium default RemoteWebDriver to log all commands executed.
 *
 * @param executor passed directly to selenium's RemoteWebDriver
 *
 * @param desiredCapabilities passed directly to selenium's RemoteWebDriver
 *
 * @param requiredCapabilities passed directly to selenium's RemoteWebDriver
 */
class TracedRemoteWebDriver(executor: CommandExecutor,
                            desiredCapabilities: Capabilities,
                            requiredCapabilities: Capabilities)
  extends RemoteWebDriver(executor, desiredCapabilities, requiredCapabilities) {

  private def prettyPrint(what: Any): String = what match {
    case str: String => s""""$str""""
    // this is needed by sendKeysToElement
    case Array(str: CharSequence) => s""""$str""""
    case null => "(null)" // scalastyle:ignore null
    case _ => what.toString
  }

  private def formatCommand(driverCommand: String, map: mutable.Map[String, _ <: Any]): String = {
    val arguments = TracedRemoteWebDriver.commandArguments.get(driverCommand)
    s"$driverCommand(${arguments.fold(map.toString)(_.map(map(_)).map(prettyPrint).mkString(", "))})"
  }

  private val elementIdsToShow = 10

  private def formatElementIds(result: Response): String = {
    // scalastyle:off field.name
    val (show, hide) = result.getValue.asInstanceOf[util.ArrayList[RemoteWebElement]].asScala.splitAt(elementIdsToShow)
    // scalastyle:on field.name
    val ids = show.map(_.getId).mkString(", ")
    val dots = hide.headOption.fold("")(_ => "...")
    s"${show.size + hide.size} [$ids$dots]"
  }

  class CommandGroup(commands: String*) {
    private val set = commands.toSet

    def unapply(command: String): Boolean = set(command)
  }

  object FormatResultElementIds extends CommandGroup(
    "findElements",
    "findChildElements"
  )

  object PrettyPrintResultValue extends CommandGroup(
    "executeScript",
    "isElementSelected",
    "isElementDisplayed",
    "getElementText",
    "getElementAttribute",
    "getElementTagName",
    "getTitle",
    "getCurrentUrl"
  )

  private def formatResult(driverCommand: String, result: Response): String = {
    driverCommand match {
      case FormatResultElementIds() => formatElementIds(result)
      case PrettyPrintResultValue() => prettyPrint(result.getValue)
      case _ => ""
    }
  }

  private def format(driverCommand: String, map: mutable.Map[String, _ <: Any], result: Try[Response]): String = {
    result match {
      case Success(success) =>
        val formatted = formatResult(driverCommand, success)
        s"execute ${formatCommand(driverCommand, map)}${if (!formatted.isEmpty) s" = $formatted" else ""}"

      case Failure(th) => s"execute ${formatCommand(driverCommand, map)} exception ${th.getMessage}"
    }
  }

  override def execute(driverCommand: String, parameters: java.util.Map[String, _]): Response = {
    Perf.printlnResult((result: Try[Response]) => format(driverCommand, parameters.asScala, result)) {
      super.execute(driverCommand, parameters)
    }
  }
}
