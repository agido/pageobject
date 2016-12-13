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
package org.pageobject.core.driver.vnc

import java.util.concurrent.atomic.AtomicInteger

import org.openqa.selenium.net.PortProber
import org.pageobject.core.tools.Logging
import org.pageobject.core.tools.OS

import scala.sys.process.BasicIO
import scala.sys.process.Process
import scala.sys.process.ProcessLogger

private object VncServer {
  val threadGroup = new ThreadGroup("VncServer")

  def createProcessLogger(stdoutName: () => String, stderrName: () => String,
                          stdout: String => Unit, stderr: String => Unit): ProcessLogger = {
    new ProcessLogger {
      def out(s: => String): Unit = {
        Thread.currentThread().setName(stdoutName())
        stdout(s)
      }

      def err(s: => String): Unit = {
        Thread.currentThread().setName(stderrName())
        stderr(s)
      }

      def buffer[T](f: => T): T = f
    }
  }
}

/**
 * trait to start and stop a VNC Server instance.
 */
trait VncServer extends Logging {
  protected def startCommand: Option[String]

  protected def checkCommand: Option[String]

  protected def stopCommand: Option[String]

  protected def onTerminated: Boolean => Unit

  protected def id: Int

  protected val processLogger: ProcessLogger =
    VncServer.createProcessLogger(stdoutName, stderrName,
      message => if (log(message)) {
        logStdOut(message)
      }, message => if (log(message)) {
        logStdErr(message)
      })

  private val traceMessages = Set(
    "Executing: ",
    "Done: "
  )

  protected def isTraceMessage(message: String): Boolean = {
    traceMessages.exists(m => message.contains(m))
  }

  protected def logStdOut(message: String): Unit = {
    if (isTraceMessage(message)) {
      trace(message)
    } else {
      debug(message)
    }
  }

  protected def logStdErr(message: String): Unit = error(message)

  protected def log(message: String): Boolean = true

  protected def execute(cmd: String, extraEnv: (String, String)*): Process = {
    Process(cmd, None, extraEnv: _*).run(BasicIO(withIn = false, processLogger).daemonized)
  }

  protected def extraEnv: Seq[(String, String)] = Seq()

  def name: String = s"VNC :$id"

  def stdoutName(): String = s"$name STDOUT"

  def stderrName(): String = s"$name STDERR"

  def shutdown(): Unit = {
    stopCommand.foreach(execute(_))
  }

  def checkConnection(): Boolean = {
    checkCommand.forall(execute(_).exitValue == 0)
  }

  def start(): Unit = startCommand match {
    case Some(command) =>
      val process = execute(command, extraEnv: _*)
      val thread = new Thread(VncServer.threadGroup, new Runnable {
        override def run(): Unit = {
          onTerminated(process.exitValue() != 127)
        }
      })
      thread.setName(s"VncServerThread-$id")
      thread.setDaemon(true)
      thread.start()
    case _ =>
  }
}

/**
 * You can configure the port range used by VNC,
 * the default is to use ports from one upwards.
 */
object CountedId {
  private val idCounter = new AtomicInteger
}

trait CountedId {
  this: VncServer =>

  val id = CountedId.idCounter.incrementAndGet()
}

/**
 * A trait providing the URL used to connect to selenium running inside of the VNC Server.
 */
trait SeleniumVncServer extends VncServer {
  def seleniumPort: Int

  protected def seleniumScript: Option[String] = sys.env.get("PAGEOBJECT_SELENIUM_SCRIPT").orElse(None match {
    case _ if OS.isOSX => Some("selenium/osx.sh")
    case _ if OS.isWindows => Some("selenium/win.bat")
    case _ if OS.isLinux => Some("selenium/linux.sh")
  })

  protected def seleniumCommand: Option[String] = seleniumScript.map(script => s"$script -port $seleniumPort")

  protected override def extraEnv = seleniumCommand.map(command => Seq("SELENIUM_COMMAND" -> command)).getOrElse(Seq())

  protected def seleniumProto: String = sys.env.getOrElse("PAGEOBJECT_SELENIUM_PROTO", "http")

  protected def seleniumHost: String = sys.env.getOrElse("PAGEOBJECT_SELENIUM_HOST", "localhost")

  lazy val url = s"$seleniumProto://$seleniumHost:$seleniumPort/wd/hub"
}

/**
 * Tries to find an unused port for selenium server.
 */
trait FindFreeSeleniumPort {
  this: SeleniumVncServer =>

  val seleniumPort: Int = PortProber.findFreePort()
}

/**
 * The default selenium port is just a fixed offset added to the display id
 *
 * This can cause problems when running multiple tests in different VMs.
 * The selenium server started by another process using the same port may be used because
 * there is no way to detect if selenium is already running or to decide if it was started by another VM
 * trying to use the same port.
 *
 * This can be fixed by monitoring the output stream of vnc.sh but it is easier to just use random port numbers.
 */
trait FixedSeleniumPort {
  this: SeleniumVncServer =>

  val seleniumPortOffset: Int = 14000
  val seleniumPort: Int = id + seleniumPortOffset
}
