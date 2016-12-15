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

// do not overload travis
concurrentRestrictions in Global := Seq(Tags.limitAll(1))

logBuffered in Test := false

// jetty 9.2.x is the last version with support java 7
val jettyVersion = "9.2.19.v20160908"
val selenium2Version = "2.53.1"
val selenium3Version = "3.0.1"
val scalatestVersion = "3.0.1"

lazy val commonSettings = Seq(
  organization := "org.pageobject",
  version := "0.2.0",
  scalaVersion := sys.env.getOrElse("TRAVIS_SCALA_VERSION", "2.11.8"),

  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",

  licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),

  homepage := Some(url("https://www.pageobject.org/")),

  publishMavenStyle := true,

  pomIncludeRepository := { _ => false },

  // use selenium 2 as default
  dependencyOverrides += "org.seleniumhq.selenium" % "selenium-java" % selenium2Version,

  publishTo <<= version { v: String =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT")) {
      Some("publish-snapshots" at nexus + "content/repositories/snapshots")
    } else {
      Some("publish-releases" at nexus + "service/local/staging/deploy/maven2")
    }
  },

  pomExtra := // scalastyle:off xml.literal
    <scm>
      <url>https://github.com/agido/pageobject.git</url>
      <connection>scm:git:git@github.com:agido/pageobject.git</connection>
    </scm>
      <developers>
        <developer>
          <name>Dennis Rieks</name>
          <organization>agido GmbH</organization>
          <organizationUrl>https://www.agido.com/</organizationUrl>
        </developer>
      </developers>,
  // scalastyle:on xml.literal

  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
)

lazy val noPublishSettings = commonSettings ++ Seq(
  publishLocal := {},
  publish := {}
)

lazy val root = (project in file("."))
  .settings(noPublishSettings: _*)
  .settings(
    name := "pageobject"
  )
  .aggregate(
    core,
    scalatest,
    testBase,
    testSeleniumShared,
    testSelenium2,
    testSelenium3,
    examples
  )

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-api" % "1.7.21",
      // remove circular dependency
      "org.seleniumhq.selenium" % "selenium-java" % selenium2Version exclude("com.codeborne", "phantomjsdriver"),
      "org.seleniumhq.selenium" % "htmlunit-driver" % "2.20" % Optional,
      "net.sourceforge.htmlunit" % "htmlunit" % "2.21" % Optional exclude("org.eclipse.jetty.websocket", "websocket-client"),

      // Warning: Class javax.annotation.Nullable not found
      "com.google.code.findbugs" % "jsr305" % selenium3Version % Optional
    )
  )

lazy val scalatest = (project in file("scalatest"))
  .settings(commonSettings: _*)
  .settings(
    name := "scalatest",
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % scalatestVersion,
      "org.pageobject.patch.org.scalatest" %% "scalatest" % scalatestVersion exclude("org.eclipse.jetty.orbit", "javax.servlet")
    )
  )
  .dependsOn(core)

lazy val testBase = (project in file("test/base"))
  .settings(noPublishSettings: _*)
  .settings(
    dependencyOverrides += "org.eclipse.jetty.websocket" % "websocket-client" % jettyVersion,

    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.1.7",
      "org.slf4j" % "jcl-over-slf4j" % "1.7.21",
      "org.easymock" % "easymock" % "3.4",
      "org.eclipse.jetty.websocket" % "websocket-client" % jettyVersion,
      "org.eclipse.jetty" % "jetty-server" % jettyVersion,
      "org.eclipse.jetty" % "jetty-webapp" % jettyVersion
    )
  ).dependsOn(scalatest)

lazy val testSeleniumShared = (project in file("test/selenium/shared"))
  .settings(noPublishSettings: _*)
  .dependsOn(testBase)

// run tests against selenium 2
lazy val testSelenium2 = (project in file("test/selenium/selenium2"))
  .settings(noPublishSettings: _*)
  .settings(
    name := "test/selenium/selenium2",
    // without this sbt won't find the shared tests...
    unmanagedSourceDirectories in Test += baseDirectory.value / "../shared/src/main/scala"
  ).dependsOn(testSeleniumShared % "test->compile")

lazy val isJdk7: Boolean = sys.props.get("java.specification.version") == Some("1.7")

// run tests against selenium 3
lazy val testSelenium3 = if (isJdk7) {
  // selenium does not support jdk7...
  // just add an empty dummy project in this case
  (project in file("test/selenium/selenium3jdk7"))
    .settings(noPublishSettings: _*)
    .settings(
      name := "test/selenium/selenium3jdk7"
    )
} else {
  (project in file("test/selenium/selenium3"))
    .settings(noPublishSettings: _*)
    .settings(
      name := "test/selenium/selenium3",
      // without this sbt won't find the shared tests...
      unmanagedSourceDirectories in Test += baseDirectory.value / "../shared/src/main/scala",
      // set selenium version to 3.x
      dependencyOverrides += "org.seleniumhq.selenium" % "selenium-java" % selenium3Version
    ).dependsOn(testSeleniumShared % "test->compile")
}

lazy val examples = (project in file("examples"))
  .settings(noPublishSettings: _*)
  .dependsOn(testBase)

updateImpactOpenBrowser in ThisBuild := false

pgpPassphrase := sys.env.get("ENCRYPTION_PASSWORD").map(_.toArray)
pgpSecretRing := baseDirectory.value / ".gnupg" / "secring.gpg"
pgpPublicRing := baseDirectory.value / ".gnupg" / "pubring.gpg"
