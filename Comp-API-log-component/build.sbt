ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"
//scalacOptions += "-Ypartial-unification"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.12.0"
libraryDependencies += "com.lihaoyi" %% "fastparse" % "3.1.0"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0"
libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "3.8.0"
)



lazy val root = (project in file("."))
  .settings(
    name := "Comp-API-log-component"
  )
