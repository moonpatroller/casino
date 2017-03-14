lazy val root = (project in file("."))
    .settings(
        name:="casino",
        organization:="com.example",
        scalaVersion:="2.12.1",
        version:="0.1.0-SNAPSHOT"
    )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

// https://mvnrepository.com/artifact/org.json4s/json4s-native_2.11
libraryDependencies += "org.json4s" % "json4s-native_2.12" % "3.5.0"

logBuffered in Test := false

// retrieveManaged := true

