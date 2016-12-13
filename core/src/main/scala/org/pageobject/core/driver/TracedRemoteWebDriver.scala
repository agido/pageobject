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
import java.util.logging.Level

import org.openqa.selenium.Capabilities
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.remote.CommandExecutor
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.RemoteWebElement
import org.openqa.selenium.remote.Response
import org.pageobject.core.tools.Logging
import org.pageobject.core.tools.Perf

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.collection.concurrent.TrieMap
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
    "quit" -> Seq(),
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
  extends RemoteWebDriver(executor, desiredCapabilities, requiredCapabilities) with Logging {

  val idFoundBy: mutable.Map[String, String] = new TrieMap[String, String]()

  private def prettyPrint(what: Any): String = what match {
    case str: String => s""""$str""""
    // this is needed by sendKeysToElement
    case Array(str: CharSequence) => s""""$str""""
    case null => "(null)" // scalastyle:ignore null
    case _ => what.toString
  }

  private def formatCommand(driverCommand: String, parameters: mutable.Map[String, _ <: Any]): String = {
    val arguments = TracedRemoteWebDriver.commandArguments.get(driverCommand)
    val map: mutable.Map[String, _ <: Any] = if (parameters.contains("id")) {
      val id = parameters("id").toString
      parameters + ("id" -> idFoundBy.getOrElse(id, id))
    } else {
      parameters
    }
    s"$driverCommand(${arguments.fold(map.toString)(_.map(map(_)).map(prettyPrint).mkString(", "))})"
  }

  private val elementIdsToShow = 10

  private def formatElementIds(driverCommand: String, parameters: mutable.Map[String, _ <: Any], result: Response):
  String = {
    val all = result.getValue.asInstanceOf[util.ArrayList[RemoteWebElement]].asScala.map(_.getId)
    val (show, hide) = all.splitAt(elementIdsToShow)
    val ids = show.mkString(", ")
    val dots = hide.headOption.fold("")(_ => "...")

    val value = parameters.getOrElse("value", "")
    val using = parameters.getOrElse("using", "")
    val prefix = parameters.get("id")
      .map(_.toString)
      .map(id => idFoundBy.get(id).map(_ + " ").getOrElse(id))
      .getOrElse("")
    all.foreach(id => idFoundBy.put(id, prefix + using match {
      case "id" => s"$id <#$value>"
      case "class name" => s"$id <.$value>"
      case "css selector" => s"$id <$value>"
      case "name" => s"$id <[name = '$value']>"
      case _ => s"$id <$using($value)>"
    }))
    s"${show.size + hide.size} Elements: [$ids$dots]"
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

  private def formatResult(driverCommand: String, parameters: mutable.Map[String, _ <: Any], result: Response): String = {
    driverCommand match {
      case FormatResultElementIds() => formatElementIds(driverCommand, parameters, result)
      case PrettyPrintResultValue() => prettyPrint(result.getValue)
      case _ => ""
    }
  }

  private def format(driverCommand: String, parameters: mutable.Map[String, _ <: Any], result: Try[Response]): String = {
    result match {
      case Success(success) =>
        val formatted = formatResult(driverCommand, parameters, success)
        s"execute ${formatCommand(driverCommand, parameters)}${if (!formatted.isEmpty) s" = $formatted" else ""}"

      case Failure(th) => s"execute ${formatCommand(driverCommand, parameters)} exception ${th.getMessage}"
    }
  }

  override def execute(driverCommand: String, parameters: java.util.Map[String, _]): Response = {
    if (driverCommand == "getLog") {
      super.execute(driverCommand, parameters)
    } else {
      if (Option(getSessionId).isDefined) {
        manage.logs.get(LogType.BROWSER).getAll.asScala.foreach(log => {
          def withoutNewLine: String = {
            val message = log.getMessage
            if (message.endsWith("\n")) {
              message.substring(0, message.length - 1)
            } else {
              message
            }
          }

          val msg = s"Console: ${log.getLevel} $withoutNewLine"
          if (log.getLevel == Level.SEVERE) {
            warn(msg)
          } else {
            info(msg)
          }
        })
      }
      Perf.logResult(debug(_), format(driverCommand, parameters.asScala, _: Try[Response])) {
        super.execute(driverCommand, parameters)
      }
    }
  }
}
