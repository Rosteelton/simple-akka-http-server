name := "evotor-terminal-notifications"

version := "1.0"

scalaVersion := "2.12.2"

val circeVersion = "0.8.0"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= Seq(
  "de.heikoseeberger" %% "akka-http-circe" % "1.16.1",
  "com.typesafe.akka" %% "akka-http" % "10.0.7",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion
)

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)