name := "scalatest"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.0-RC3" exclude("org.eclipse.jetty.orbit", "javax.servlet")
)
