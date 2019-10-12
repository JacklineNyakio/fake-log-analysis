val sharedSettings = Seq(
  organization := "io.logAnalysis",
  version := "0.1.0",
  scalaVersion := "2.11.12",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:implicitConversions"
  ),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    "Typesafe repository releases"           at "http://repo.typesafe.com/typesafe/releases/",
    "Confluent Maven Repository"             at "http://packages.confluent.io/maven/",
    "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
    "Fabricator"                             at "http://dl.bintray.com/biercoff/Fabricator",
    "Spark Packages Repo"                    at "https://dl.bintray.com/spark-packages/maven"
  ),
  dependencyOverrides ++= Seq(
    "com.fasterxml.jackson.core"    %  "jackson-core"          % "2.9.9",
    "com.fasterxml.jackson.core"    %  "jackson-databind"      % "2.9.9",
    "com.fasterxml.jackson.core"    %  "jackson-annotations"   % "2.9.9",
    "com.fasterxml.jackson.module"  %% "jackson-module-scala"  % "2.9.9"
  )
)

lazy val kafkaVersion    = "2.3.0"
lazy val sparkVersion    = "2.4.3"

val sharedDependecies = Seq(
  libraryDependencies ++= Seq(
    "io.spray"                      %%  "spray-json"             % "1.3.5",
    "com.typesafe.scala-logging"    %%  "scala-logging"          % "3.9.0",
    "org.slf4j"                     %   "slf4j-simple"           % "1.7.25",
    "joda-time"                     %   "joda-time"              % "2.10.4",
    "org.apache.avro"               %   "avro"                   % "1.9.0",
    "org.apache.avro"               %   "avro-tools"             % "1.9.0",
    "com.sksamuel.avro4s"           %%   "avro4s-core"           % "1.8.3"
  )
)
lazy val logAnalysis = (project in file(".")).aggregate(core, compute, source, web)

lazy val core = (project in file("core")).settings(
  sharedSettings,
  sharedDependecies,
  libraryDependencies ++= Seq(
    "com.typesafe"                 %   "config"                    % "1.3.0",
    "com.github.azakordonets"      %   "fabricator_2.11"           % "2.1.5",
    "io.spray"                     %%  "spray-json"                % "1.3.4",
    "org.scalaz"                   %%  "scalaz-core"               % "7.2.28"
  )
)
lazy val source = (project in file("source")).settings(
  name := "data-source",
  sharedSettings,
  sharedDependecies,
  libraryDependencies ++= Seq(
    "org.apache.kafka"            % "kafka-streams"         % "2.3.0",
    "org.apache.kafka"            % "kafka-clients"         % "2.3.0",
    "io.confluent"                % "kafka-avro-serializer" % "5.2.2"
  )
).dependsOn(core)

lazy val compute = (project in file("compute")).settings(
  name := "spark-jobs",
  sharedSettings,
  libraryDependencies ++= Seq(
    "io.netty"                      % "netty-all"                       % "4.1.17.Final",
    "org.apache.spark"             %% "spark-core"                      % sparkVersion     % Compile,
    "org.apache.spark"             %% "spark-sql"                       % sparkVersion     % Compile,
    "org.apache.spark"             %% "spark-streaming"                 % sparkVersion     % Compile,
    "org.apache.spark"             %  "spark-streaming-kafka-0-10_2.11" % sparkVersion     % Compile,
    "org.apache.spark"             %  "spark-sql-kafka-0-10_2.11"       % sparkVersion     % Compile,
    "com.datastax.spark"           %% "spark-cassandra-connector"       % "2.4.1"          % Compile
  )
).dependsOn(core,source)

lazy val web = (project in file("web")).settings(
  name := "http-endpoint",
  sharedSettings,
  libraryDependencies ++= Seq(

  )
).dependsOn(core, source, compute)