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

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

import org.pageobject.core.WaitFor
import org.pageobject.core.tools.Environment
import org.pageobject.core.tools.Logging

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.concurrent.blocking

/**
 * VncManager will start a new VncServer when needed.
 *
 * A previously released VNC Server will be cached and reused if needed.
 *
 * Because it is not safe to releay on shutdownAll,
 * the VncServer should shutdown itself then the parent process has gone.
 */
case class VncServerManager[V <: VncServer](createVncServer: (Boolean => Unit) => V) extends WaitFor with Logging {
  // VNC servers currently in use
  private val running = mutable.Set[V]()

  // VNC servers that can be reused
  private val free = mutable.Set[V]()

  private val count = new AtomicInteger()
  private val waiting = new LinkedBlockingQueue[Promise[V]]()

  private val cleanupId = new AtomicInteger()

  private def startCleanup(): Unit = {
    val id = cleanupId.incrementAndGet()
    Future {
      Thread.sleep(2000) // scalastyle:ignore magic.number
      VncServerManager.synchronized {
        if (cleanupId.get() == id) {
          if (running.isEmpty && waiting.isEmpty) {
            shutdownAll()
          }
        }
      }
    }
  }

  private def tryStart(): AtomicReference[Either[Option[V], Boolean]] = {
    val ref = new AtomicReference[Either[Option[V], Boolean]](Left(None))
    val ret: V = createVncServer(retry => VncServerManager.synchronized {
      free -= ref.get.left.get.get
      running -= ref.get.left.get.get
      ref.set(Right(retry))
    })
    ref.set(Left(Some(ret)))
    ret.start()
    ref
  }

  //noinspection NoTailRecursionAnnotation
  private def start(retryCount: Int = 10, seconds: Int = 5000, each: Int = 1000, step: Int = 100): V = // scalastyle:ignore magic.number
  {
    if (retryCount <= 0) {
      error("failed to start vnc server, have a look at VncServer's logging above!")
      throw new RuntimeException("failed to start vnc server!")
    }
    val vncServerRef = tryStart()
    for {
      step <- 0 to seconds by step
    } {
      Thread.sleep(step)
      // check every 100ms if startup has failed
      vncServerRef.get() match {
        case Right(true) | Left(None) => return start(retryCount - 1) // scalastyle:ignore return
        case Right(false) => return start(-1) // scalastyle:ignore return
        case Left(Some(vncServer)) =>
          if (step % each == 0) {
            // check every second if startup has succeeded
            if (vncServer.checkConnection()) {
              running += vncServer
              return vncServer // scalastyle:ignore return
            }
          }
      }
    }
    start(retryCount - 1)
  }

  private def startNewVncServer(): Future[V] = {
    count.incrementAndGet()
    Future {
      blocking {
        start()
      }
    }
  }

  private def freeVncServer(): Future[V] = {
    val vncServer = free.head
    free -= vncServer
    running += vncServer
    Future(vncServer)
  }

  def getOrCreateFreeVncServer(): Future[V] = VncServerManager.synchronized {
    if (free.isEmpty) {
      Environment.integer("PAGEOBJECT_VNC_LIMIT") match {
        case Some(limit) if count.get() >= limit =>
          val promise = Promise[V]()
          waiting.put(promise)
          promise.future

        case _ =>
          startNewVncServer()
      }
    } else {
      freeVncServer()
    }
  }

  /**
   * Release the given VNC server. Will that the Server to the list of reuseable VNC servers.
   *
   * @param vncServer VncServer to release
   */
  def release(vncServer: V): Unit = VncServerManager.synchronized {
    if (waiting.isEmpty) {
      running -= vncServer
      free += vncServer
      startCleanup()
    } else {
      waiting.take().success(vncServer)
    }
  }

  /**
   * Calls shutdown for all managed VncServers.
   *
   * Warning: It is not safe to releay on this, the VNC server should watch the PPID
   * (See comment of DefaultVncServer for details)
   */
  def shutdownAll(): Unit = VncServerManager.synchronized {
    running.foreach(_.shutdown())
    running.clear()
    free.foreach(_.shutdown())
    free.clear()
  }
}
