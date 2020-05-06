lazy val root = project
  .in(file("."))
  .settings(
    name := "pdt_typesummon",
    version := "0.1.0",
    resolvers += Resolver.mavenLocal,

    scalaVersion := dottyLatestNightlyBuild.get,

    libraryDependencies ++= Seq(
      "ch.epfl.lamp" % "dotty_0.25" % (scalaVersion.value)
    )
  )
