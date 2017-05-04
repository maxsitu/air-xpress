import java.text.SimpleDateFormat
import java.util.Date

import scala.util.Try

name := """jt-account-service"""

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

enablePlugins(BuildInfoPlugin)

// Logging
val slf4jVersion        = "1.7.21"
val logBackVersion      = "1.1.7"
val scalaLoggingVersion = "3.5.0"

val slf4jApi       = "org.slf4j" % "slf4j-api" % slf4jVersion
val logBackClassic = "ch.qos.logback" % "logback-classic" % logBackVersion
val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
val loggingStack   = Seq(slf4jApi, logBackClassic, scalaLogging)

// Akka
val akkaV           = "2.5.0"
val akkaHttpVersion = "10.0.4"

val akkaHttpCore         = "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
val akkaHttpExperimental = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
val akkaHttpTestkit      = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test"
val akkaHttpSession      = "com.softwaremill.akka-http-session" %% "core" % "0.4.0"
val akkaHttpCirce        = "de.heikoseeberger" %% "akka-http-circe" % "1.15.0"
val akkaStack            = Seq(akkaHttpCore, akkaHttpExperimental, akkaHttpTestkit, akkaHttpSession, akkaHttpCirce)

val swagger              = "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.9.1"

// Slick
val slickVersion          = "3.2.0"

val slick       = "com.typesafe.slick" %% "slick" % slickVersion
val slickHikari = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
val h2          = "com.h2database" % "h2" % "1.3.176" //watch out! 1.4.190 is beta
val postgres    = "org.postgresql" % "postgresql" % "9.4.1208"
val flyway      = "org.flywaydb" % "flyway-core" % "4.0"
val slickStack  = Seq(slick, h2, postgres, slickHikari, flyway)

val typesafeConfig = "com.typesafe" % "config" % "1.3.1"

val scalatest        = "org.scalatest" %% "scalatest" % "3.0.1" % "test"
val unitTestingStack = Seq(scalatest)

libraryDependencies ++= unitTestingStack ++ loggingStack ++ akkaStack ++ slickStack ++ Seq(
  typesafeConfig,
  swagger,
  "mysql" % "mysql-connector-java" % "6.0.6",
  "com.github.t3hnar"   %%  "scala-bcrypt"  % "2.4",
  "org.mindrot"         %   "jbcrypt"        % "0.3m"
)

val circeVersion = "0.7.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)
buildInfoPackage := "com.jc.account.version"
buildInfoObject := "BuildInfo"
buildInfoKeys := Seq[BuildInfoKey](
  BuildInfoKey.action("buildDate")(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())),
  // if the build is done outside of a git repository, we still want it to succeed
  BuildInfoKey.action("buildSha")(Try(Process("git rev-parse HEAD").!!.stripLineEnd).getOrElse("?"))
)

mainClass in Compile := Some("com.jc.account.Main")

Revolver.settings
RenameProject.settings