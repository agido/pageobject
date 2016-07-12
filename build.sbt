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
  scalaVersion := "2.11.8",

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
    test
  )

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)

lazy val scalatest = (project in file("scalatest"))
  .settings(commonSettings: _*)
  .dependsOn(core)

lazy val test = (project in file("test"))
  .settings(commonSettings: _*)
  .settings(noPublishSettings: _*)
  .dependsOn(scalatest)

pgpPassphrase := sys.env.get("ENCRYPTION_PASSWORD").map(_.toArray)
pgpSecretRing := baseDirectory.value / ".gnupg" / "secring.gpg"
pgpPublicRing := baseDirectory.value / ".gnupg" / "pubring.gpg"
