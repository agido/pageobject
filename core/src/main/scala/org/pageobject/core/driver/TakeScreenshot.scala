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

import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.atomic.AtomicInteger

import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver

/**
 * Helper object to capture screenshots on test failure and store them into the local filesystem
 */
private object TakeScreenshot {
  private val screenshotCounter = new AtomicInteger
  private val screenshotDate = new SimpleDateFormat("yyyy-MM-dd_HHmm").format(new Date())
  private val screenshotsDir = new File("screenshots", screenshotDate)

  private def screenshotId = screenshotCounter.getAndIncrement()

  private def replaceInvalidChars(string: String): String = {
    string.replaceAll(":", "-").replaceAll(" ", "_").replaceAll("-_", "-")
  }

  private def createScreenshotFiles(testName: String): (File, File) = {
    screenshotsDir.mkdirs()
    val base = replaceInvalidChars(s"$screenshotId-$testName")
    val png = s"$base.png"
    val html = s"$base.html"
    System.err.println(s"Screenshot Image: file:///screenshots/$screenshotDate/$png")
    System.err.println(s"Screenshot HTML: file:///screenshots/$screenshotDate/$html")
    (new File(screenshotsDir, png), new File(screenshotsDir, html))
  }

  private def writeToFile(file: File, content: Array[Byte]): Unit = {
    val fos1 = new FileOutputStream(file)
    try fos1.write(content)
    finally fos1.close()
  }

  def writeFiles(testName: String, png: Array[Byte], html: Array[Byte]): Unit = {
    // scalastyle:off field.name
    val (pngFile, htmlFile) = createScreenshotFiles(testName)
    // scalastyle:on field.name
    writeToFile(pngFile, png)
    writeToFile(htmlFile, html)
  }
}

/**
 * When a WebDriver implements this trait, a screenshot will be stored on disk when a test failes.
 */
trait TakeScreenshot {
  this: DriverFactory =>

  override def takeScreenshot(testName: String, webDriver: WebDriver with TakesScreenshot): Unit = {
    val png = webDriver.getScreenshotAs(OutputType.BYTES)
    val html = webDriver.getPageSource.getBytes
    TakeScreenshot.writeFiles(testName, png, html)
  }
}
