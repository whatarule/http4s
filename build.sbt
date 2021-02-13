import Dependencies._

ThisBuild / scalaVersion     := "2.12.13"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val http4sVersion = "0.21.15"

lazy val root = (project in file("."))
  .settings(
    name := "sample",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.typelevel" %% "cats-core" % "2.1.1",

      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,

      "com.softwaremill.sttp.tapir" %% "tapir-core" % "0.17.10",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "0.17.10",
    ),
    scalacOptions ++= Seq("-Ypartial-unification"),
  )

