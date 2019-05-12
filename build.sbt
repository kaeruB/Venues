import play.sbt.PlaySettings
import sbt.Keys._

lazy val GatlingTest = config("gatling") extend Test

scalaVersion in ThisBuild := "2.12.8"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, GatlingPlugin)
  .configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
  .settings(
    scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation",
    name := "Venues",
    libraryDependencies ++= Seq(
      guice,
      "com.typesafe.play" %% "play" % "2.7.0",
      "io.lemonlabs" %% "scala-uri" % "1.4.4",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test",
      "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.0.1.1" % "test",
      "io.gatling" % "gatling-test-framework" % "3.0.1.1" % Test
    )
  )