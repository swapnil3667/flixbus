

import sbt.Keys.fullClasspath

name := "flixbus"
version := "0.1"
scalaVersion := "2.12.8"
organization := "com.flixbus"

resolvers ++= Seq (
  "confluent" at "http://packages.confluent.io/maven/",
  "spark-packages" at "https://dl.bintray.com/spark-packages/maven/"
)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.4.0",
  "org.apache.kafka" % "connect-json" % "0.9.0.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.9",
  "org.json4s" %% "json4s-native" % "3.5.1",
  "org.json4s" %% "json4s-jackson" % "3.5.1",
  "org.yaml" % "snakeyaml" % "1.11"
)

mergeStrategy in assembly := {

  case PathList("org", "apache", "spark", "unused", "UnusedStubClass.class")
  => MergeStrategy.first

  case x => (mergeStrategy in assembly).value(x)
}

Compile / run := Defaults.runTask(Compile / fullClasspath, Compile / run / mainClass, Compile / run / runner).evaluated
