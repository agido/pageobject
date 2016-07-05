name := "core"

libraryDependencies ++= Seq(
  "org.seleniumhq.selenium" % "selenium-java" % "2.53.0",
  "org.seleniumhq.selenium" % "htmlunit-driver" % "2.20",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.21" exclude("org.eclipse.jetty.websocket", "websocket-client"),

  // Warning: Class javax.annotation.Nullable not found
  "com.google.code.findbugs" % "jsr305" % "3.0.1"
)
