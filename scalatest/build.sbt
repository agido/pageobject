name := "scalatest"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.0-RC4",
  "org.pageobject.patch.org.scalatest" %% "scalatest" % "3.0.0-SNAPSHOT" exclude("org.eclipse.jetty.orbit", "javax.servlet")
)
