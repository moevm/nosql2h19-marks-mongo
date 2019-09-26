name := "backend"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.7.0",
  "com.typesafe.akka" %% "akka-http" % "10.1.7",
  "com.typesafe.akka" %% "akka-stream" % "2.5.21",
  "com.typesafe.akka" %% "akka-actor" % "2.5.21",
  "org.json4s" %% "json4s-jackson" % "3.6.5",
  "com.tethys-json" %% "tethys" % "0.9.0.1",
  "com.tethys-json" %% "tethys-json4s" % "0.9.0.1"
)