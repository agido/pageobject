addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.1")
addSbtPlugin("com.updateimpact" % "updateimpact-sbt-plugin" % "2.1.1")

sys.env.isDefinedAt("SCOVER") match {
  case true =>
    Seq(
      addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5"),
      addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.1.0")
    )

  case _ =>
    Seq()
}
