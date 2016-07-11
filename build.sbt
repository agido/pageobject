lazy val commonSettings = Seq(
  organization := "org.pageobject",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.8",

  resolvers +=
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",

  publishTo <<= version { v: String =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT")) {
      Some("publish-snapshots" at nexus + "content/repositories/snapshots")
    } else {
      Some("publish-releases" at nexus + "service/local/staging/deploy/maven2")
    }
  },

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
