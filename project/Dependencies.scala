import sbt._

object Dependencies {
  lazy val akkaHttpVersion = "10.0.10"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"
  lazy val akkaHttp  = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  lazy val json4s    = "org.json4s" %% "json4s-native" % "3.5.3"
  lazy val sprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
  lazy val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
}
