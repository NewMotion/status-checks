import Dependencies._
import sbt.Keys.scalaVersion

name := "statusChecks"

lazy val commonSettings = Seq(
  organization := "com.newmotion",
  scalaVersion := tnm.ScalaVersion.curr,
  crossScalaVersions := Seq(tnm.ScalaVersion.curr, tnm.ScalaVersion.prev)
)

mainClass in Compile := (mainClass in Compile in example).value

lazy val statusChecks = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(core, akka_http_spray_json, example)

lazy val core = project
  .enablePlugins(OssLibPlugin)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies += scalaTest % Test
  )

lazy val akka_http_spray_json = project
  .enablePlugins(OssLibPlugin)
  .dependsOn(core)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(akkaHttp, sprayJson, scalaTest % Test, akkaHttpTestkit % Test)
  )

lazy val example = project
  .dependsOn(core)
  .settings(commonSettings: _*)
  .settings(
    mainClass in Compile := Some("com.newmotion.devops.Main"),
    publish := {}
  )
