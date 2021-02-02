// Scala version from WPO's
name := "Lennert-Bontinck-SA1"
version := "1.0"
scalaVersion := "2.13.3"

// Needed libraries, same versions from WPO's
val AkkaVersion = "2.5.32"
val AlpakkaVersion = "2.0.2"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % AlpakkaVersion
)
