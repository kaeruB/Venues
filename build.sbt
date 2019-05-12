scalaVersion in ThisBuild := "2.12.8"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "Venues",
    libraryDependencies ++= Seq(
      guice,
      "com.typesafe.play" %% "play" % "2.7.0",
      "io.lemonlabs" %% "scala-uri" % "1.4.4",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )
