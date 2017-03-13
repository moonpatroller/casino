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

// https://mvnrepository.com/artifact/com.typesafe.play/play-json_2.11
// libraryDependencies += "com.typesafe.play" % "play-json_2.11" % "2.6.0-M5"

// https://mvnrepository.com/artifact/org.json4s/json4s-native_2.11
libraryDependencies += "org.json4s" % "json4s-native_2.12" % "3.5.0"

logBuffered in Test := false
