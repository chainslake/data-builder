scalaVersion := "2.12.18"
name := "chainslake-app"


libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.1"
libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.4.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test
libraryDependencies += "com.github.ajrnz" %% "scemplate" % "0.5.1"
libraryDependencies += "org.apache.spark" %% "spark-hive" % "3.5.1"
libraryDependencies += "io.delta" %% "delta-spark" % "3.2.0"
