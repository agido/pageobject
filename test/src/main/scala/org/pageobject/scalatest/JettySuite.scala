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
package org.pageobject.scalatest

import java.util.concurrent.TimeUnit

import org.eclipse.jetty.server.Connector
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler
import org.eclipse.jetty.webapp.WebAppContext
import org.pageobject.core.page.DomainPage
import org.pageobject.core.page.PageObject
import org.scalatest.Suite

import scala.concurrent.Await
import scala.concurrent.Promise
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

object JettySuite {
  val jettyThreadGroup = new ThreadGroup("jetty")

  case class JettyUrl(domain: String)

  abstract class JettyPage(implicit jettyUrl: JettyUrl) extends PageObject with DomainPage {
    final val domain = jettyUrl.domain
  }

  private val timeout = FiniteDuration(30, TimeUnit.SECONDS) // scalastyle:ignore magic.number

  private def startJettyServer(webAppContext: String): Int = {
    val ret = Promise[Int]()
    val name = s"jetty $webAppContext"
    val group = new ThreadGroup(jettyThreadGroup, name)
    group.setDaemon(true)
    val thread = new Thread(new Runnable {
      override def run(): Unit = {
        ret.complete(Try {
          val pool = new QueuedThreadPool(200, 8, 60000, None.orNull, group) // scalastyle:ignore magic.number
          val scheduler = new ScheduledExecutorScheduler(None.orNull, false, None.orNull, group)
          val server = new Server(pool)
          server.addBean(scheduler)
          val connector = new ServerConnector(server)
          connector.setPort(0)
          server.setConnectors(Array[Connector](connector))
          val context = new WebAppContext(webAppContext, "/")
          server.setHandler(context)
          server.start()

          while (!server.isStarted) // scalastyle:ignore while
            Thread.sleep(10) // scalastyle:ignore magic.number

          val port = server.getConnectors()(0).asInstanceOf[ServerConnector].getLocalPort
          Thread.currentThread().setName(s"name:$port")
          port
        })
      }
    }, name)
    thread.setDaemon(true)
    thread.start()
    Await.result(ret.future, timeout)
  }

  private val instances = collection.concurrent.TrieMap[String, Int]()

  def getJettyServerPort(webAppContext: String): Int = {
    instances.getOrElseUpdate(webAppContext, startJettyServer(webAppContext))
  }
}

trait JettySuite extends Suite {
  protected def webAppContext: String

  private def port = JettySuite.getJettyServerPort(webAppContext)

  implicit def jettyUrl: JettySuite.JettyUrl = JettySuite.JettyUrl(s"http://localhost:$port")
}
