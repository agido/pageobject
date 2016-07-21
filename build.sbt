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

lazy val commonSettings = Seq(
  organization := "org.pageobject",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := sys.env.getOrElse("TRAVIS_SCALA_VERSION", "2.11.8"),

  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",

  licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),

  homepage := Some(url("https://www.pageobject.org/")),

  publishMavenStyle := true,

  pomIncludeRepository := { _ => false },

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
      <url>https://github.com/implydata/wikiticker.git</url>
      <connection>scm:git:git@github.com:implydata/wikiticker.git</connection>
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

lazy val noPublishSettings = Seq(
  publishLocal := {},
  publish := {}
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(
    name := "pageobject"
  )
  .aggregate(
    core,
    scalatest,
    test,
    examples
  )

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "org.seleniumhq.selenium" % "selenium-java" % "2.53.0",
      "org.seleniumhq.selenium" % "htmlunit-driver" % "2.20" % Optional,
      "net.sourceforge.htmlunit" % "htmlunit" % "2.21" % Optional exclude("org.eclipse.jetty.websocket", "websocket-client"),

      // Warning: Class javax.annotation.Nullable not found
      "com.google.code.findbugs" % "jsr305" % "3.0.1" % Optional
    )
  )

lazy val scalatest = (project in file("scalatest"))
  .settings(commonSettings: _*)
  .settings(
    name := "scalatest",
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.0.0-RC4",
      "org.pageobject.patch.org.scalatest" %% "scalatest" % "3.0.0-SNAPSHOT" exclude("org.eclipse.jetty.orbit", "javax.servlet")
    )
  )
  .dependsOn(core)

val jettyVersion = "9.3.9.v20160517"

lazy val test = (project in file("test"))
  .settings(commonSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(
    name := "test",
    libraryDependencies ++= Seq(
      "org.easymock" % "easymock" % "3.4",
      "org.eclipse.jetty.websocket" % "websocket-client" % jettyVersion,
      "org.eclipse.jetty" % "jetty-server" % jettyVersion,
      "org.eclipse.jetty" % "jetty-webapp" % jettyVersion
    )
  )
  .dependsOn(scalatest)

lazy val examples = (project in file("examples"))
  .settings(commonSettings: _*)
  .settings(noPublishSettings: _*)
  .dependsOn(test)

updateImpactOpenBrowser in ThisBuild := false

pgpPassphrase := sys.env.get("ENCRYPTION_PASSWORD").map(_.toArray)
pgpSecretRing := baseDirectory.value / ".gnupg" / "secring.gpg"
pgpPublicRing := baseDirectory.value / ".gnupg" / "pubring.gpg"
