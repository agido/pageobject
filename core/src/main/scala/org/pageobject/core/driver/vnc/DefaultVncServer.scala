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

/**
 * This class controls the startup of new VNC Servers.
 *
 * Default start/check/stop command lines are provided here.
 *
 * You can configure the used script by setting the environment variable PAGEOBJECT_VNC_SCRIPT.
 *
 * It is recommented that the VNC script will watch the parent process id (\$PPID in bash) and
 * shutdown the VNC Server when the process has finished.
 *
 * Because you can easly stop the java process using the debugger no additional cleanup is required
 * to terminate the VNC server cleanly when watching the PPID
 */
case class DefaultVncServer(onTerminated: Boolean => Unit) extends SeleniumVncServer with CountedId with FixedSeleniumPort {
  private val display = sys.env.getOrElse("DISPLAY", "")

  protected val script: String = sys.env.getOrElse("PAGEOBJECT_VNC_SCRIPT", "vnc/vnc.sh")

  protected val startCommand: Option[String] = Some(s"$script start :$id $display")

  protected val checkCommand: Option[String] = Some(s"$script check :$id $seleniumPort")

  protected val stopCommand: Option[String] = None
}

/**
 * A VncServerManager creating DefaultVncServer instances.
 */
object DefaultVncServerManager extends VncServerManager(DefaultVncServer)
