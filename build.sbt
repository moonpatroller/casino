lazy val root = (project in file("."))
    .settings(
        name:="casino",
        organization:="com.example",
        scalaVersion:="2.12.1",
        version:="0.1.0-SNAPSHOT"
    )

lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.13.4"
libraryDependencies += scalacheck % Test

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

logBuffered in Test := false
