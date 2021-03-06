lazy val root = (project in file(".")).
  settings(
    organization := "org.scavenger",
    name := "scavenger",
    version := "2.1",
    scalaVersion := "2.10.4",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-actor_2.10" % "2.3.9",
      "com.typesafe.akka" % "akka-contrib_2.10" % "2.3.9",
      "com.typesafe" % "config" % "1.3.0-M1",
      "org.scala-lang" % "scala-swing" % "2.10.4"
    ),
    scalacOptions ++= Seq("-feature"),
    scalacOptions in (Compile,doc) ++= Seq("-groups", "-implicits"),
    packAutoSettings
  )
