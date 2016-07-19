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
import org.scalatest.Suite

object JettySuite {

  case class JettyUrl(domain: String)

  abstract class JettyPage(implicit jettyUrl: JettyUrl) extends PageObject with DomainPage {
    final val domain = jettyUrl.domain
  }

  private def startJettyServer(webAppContext: String): Int = {
    val server = new Server(0)
    val context = new WebAppContext(webAppContext, "/")
    server.setHandler(context)
    server.start()

    while (!server.isStarted) // scalastyle:ignore while
      Thread.sleep(10) // scalastyle:ignore magic.number

    server.getConnectors()(0).asInstanceOf[ServerConnector].getLocalPort
  }

  private val instances = collection.concurrent.TrieMap[String, Int]()

  def getJettyServerPort(webAppContext: String): Int = {
    instances.getOrElseUpdate(webAppContext, startJettyServer(webAppContext))
  }
}

trait JettySuite extends Suite {
  protected def webAppContext = "webapp"

  private def port = JettySuite.getJettyServerPort(webAppContext)

  implicit def jettyUrl: JettySuite.JettyUrl = JettySuite.JettyUrl(s"http://localhost:$port")
}
