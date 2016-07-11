name := "test"

val jettyVersion = "9.3.9.v20160517"

libraryDependencies ++= Seq(
  "org.eclipse.jetty.websocket" % "websocket-client" % jettyVersion % "test",
  "org.eclipse.jetty" % "jetty-server" % jettyVersion % "test",
  "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "test"
)
