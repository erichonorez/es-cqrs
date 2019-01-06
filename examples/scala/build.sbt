lazy val root = project aggregate(
  complete,
  incomplete
)
lazy val complete = project settings(
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.19",
  libraryDependencies += "com.lihaoyi" %% "pprint" % "0.5.3",
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
)
lazy val incomplete = project settings(
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.19",
  libraryDependencies += "com.lihaoyi" %% "pprint" % "0.5.3",
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
)