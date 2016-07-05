/*
 * Copyright 2001-2013 Artima, Inc.
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

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.webapp.WebAppContext
import org.pageobject.core.page.DomainPage
import org.pageobject.core.page.PageObject
import org.scalatest.Args
import org.scalatest.Status
import org.scalatest.Suite

object JettySuite {

  case class JettyUrl(domain: String)

  abstract class JettyPage(implicit jettyUrl: JettyUrl) extends PageObject with DomainPage {
    final val domain = jettyUrl.domain
  }

}

trait JettySuite extends Suite {
  protected val webAppContext = "webapp"

  private object serverThread extends Thread {
    private val server = new Server(0)

    override def run(): Unit = {
      val webapp = new WebAppContext(webAppContext, "/")
      server.setHandler(webapp)
      server.setStopAtShutdown(true)
      server.start()
      server.join()
    }

    def done(): Unit = {
      server.stop()
    }

    def isStarted: Boolean = server.isStarted

    def port: Int = serverThread.server.getConnectors()(0).asInstanceOf[ServerConnector].getLocalPort
  }

  implicit def jettyUrl: JettySuite.JettyUrl = JettySuite.JettyUrl(s"http://localhost:${serverThread.port}")

  override def run(testName: Option[String], args: Args): Status = {
    serverThread.start()
    while (!serverThread.isStarted) // scalastyle:ignore while
      Thread.sleep(10) // scalastyle:ignore magic.number

    try super.run(testName, args)
    finally serverThread.done()
  }
}
