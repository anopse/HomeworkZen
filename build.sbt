name := "HomeworkZen"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.17"
libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % "2.4.17"
libraryDependencies += "com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.17"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.5"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5"
libraryDependencies += "com.github.dnvriend" %% "akka-persistence-jdbc" % "2.4.18.1"
libraryDependencies += "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.9.1"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.+"
libraryDependencies += "com.github.romix.akka" %% "akka-kryo-serialization" % "0.5.1"
libraryDependencies += "mysql" % "mysql-connector-java" % "6.0.6"

addCompilerPlugin("com.github.ghik" %% "silencer-plugin" % "0.5")
libraryDependencies += "com.github.ghik" %% "silencer-lib" % "0.5"
