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
package org.pageobject.scalatest

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

import org.pageobject.core.tools.Limit.Limit
import org.pageobject.core.tools.Limit.TestLimit
import org.pageobject.core.tools.LimitProvider
import org.scalatest.Args
import org.scalatest.PageObjectHelper
import org.scalatest.ParallelTestExecution
import org.scalatest.Status
import org.scalatest.Suite
import org.scalatest.SuiteMixin

import scala.collection.mutable

/**
 * Management of Threads used for testRuns.
 * Provides two special thread pools: singleThreadPool and unlimitedPool
 */
object ParallelTestLimit {
  private val atomicThreadCounter = new AtomicInteger

  private val threadFactory = new ThreadFactory {
    val defaultThreadFactory = Executors.defaultThreadFactory

    def newThread(runnable: Runnable): Thread = {
      val thread = defaultThreadFactory.newThread(runnable)
      thread.setName("ScalaTest-" + atomicThreadCounter.incrementAndGet())
      thread
    }
  }

  private[scalatest] lazy val unlimitedPool: ExecutorService = Executors.newCachedThreadPool(threadFactory)
  private[scalatest] lazy val singleThreadPool: ExecutorService = createThreadPool(1)

  def createThreadPool(poolSize: Int): ExecutorService = {
    Executors.newFixedThreadPool(poolSize, threadFactory)
  }
}

/**
 * Management of thread pools by name.
 *
 * Can e.g. create a Thread Pool "Firefox" with 2 threads and another one called "Chrome" with 1 thread.
 */
object ConfigureableParallelTestLimit {
  private def createPool(limit: Limit): ExecutorService = limit.get match {
    case -1 => ParallelTestLimit.unlimitedPool
    case max: Int => ParallelTestLimit.createThreadPool(max)
  }

  private val map = mutable.Map[String, ExecutorService]()

  def getPool(limit: Limit): ExecutorService = synchronized {
    val selected = if (limit.env.isDefined) {
      limit
    } else {
      TestLimit
    }
    map.getOrElseUpdate(selected.name, createPool(selected))
  }
}

/**
 * Use this trait to override scalatests Distributor argument.
 *
 * The default Distributor can only create up to 16 threads, if you want
 * to run more (e.g. when running a stress test) you need this trait.
 */
trait ParallelTestLimit extends SuiteMixin with ParallelTestExecution {
  this: Suite =>

  def executorService: ExecutorService

  abstract override def run(testName: Option[String], args: Args): Status = {
    super.run(testName, args.copy(distributor = Some(PageObjectHelper.concurrentDistributor(args, executorService))))
  }
}

/**
 * When using this trait, a unlimited number of test can be executed at the same time
 */
trait UnlimitedParallelTestRuns extends ParallelTestLimit {
  this: Suite =>

  val executorService = ParallelTestLimit.unlimitedPool
}

/**
 * When using this trait, only one test can be executed at the same time
 */
trait SingleThreadedTestRun extends ParallelTestLimit {
  this: Suite =>

  val executorService = ParallelTestLimit.singleThreadPool
}

/**
 * When using this trait, you can configure the number of test
 * that can be executed in parallel by using environment variables.
 *
 * Example:
 * TEST_LIMIT=2
 * Allow two tests to be executed at the same time.
 *
 * @see BrowserLimitSuite
 */
trait ConfigureableParallelTestLimit extends ParallelTestLimit {
  this: Suite with LimitProvider =>

  val executorService = ConfigureableParallelTestLimit.getPool(limit)
}
