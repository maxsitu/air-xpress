import java.text.SimpleDateFormat
import java.util.Date

import sbt.Keys.version
import sbtbuildinfo.BuildInfoPlugin.autoImport.buildInfoPackage

import scala.sys.process.Process
import scala.util.Try
import complete.DefaultParsers._

val slf4jVersion        = "1.7.21"
val logBackVersion      = "1.1.7"
val scalaLoggingVersion = "3.5.0"
val akkaV               = "2.5.6"
val akkaHttpVersion     = "10.0.10"
val slickVersion        = "3.2.1"

lazy val updateNpm = taskKey[Unit]("Update npm")
lazy val npmTask   = inputKey[Unit]("Run npm with arguments")

lazy val commonSettings = Seq(
  name := """jt-api-service""",
  version := "2.1",
  scalaVersion := "2.11.7",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
  updateNpm := {
    println("Updating npm dependencies")
    haltOnCmdResultError(Process("npm install", baseDirectory.value / ".." / "ui").!)
  },
  npmTask := {
    val taskName = spaceDelimited("<arg>").parsed.mkString(" ")
    updateNpm.value
    val localNpmCommand = "npm " + taskName
    def buildWebpack() =
      Process(localNpmCommand, baseDirectory.value / ".." / "ui").!
    println("Building with Webpack : " + taskName)
    haltOnCmdResultError(buildWebpack())
  }
)


def haltOnCmdResultError(result: Int) {
  if (result != 0) {
    throw new Exception("Build failed.")
  }
}

val scaladOptions = taskKey[Seq[String]]("Options for the Scala compiler.")

// Logging
val slf4jApi        = "org.slf4j" % "slf4j-api" % slf4jVersion
val logBackClassic  = "ch.qos.logback" % "logback-classic" % logBackVersion
val scalaLogging    = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
val loggingStack    = Seq(slf4jApi, logBackClassic, scalaLogging)

// Akka
val akkaHttpCore    = "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
val akkaHttpExperimental = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test"
val akkaHttpSession = "com.softwaremill.akka-http-session" %% "core" % "0.4.0"
val akkaHttpCirce   = "de.heikoseeberger" %% "akka-http-circe" % "1.15.0"
val akkaStack       = Seq(akkaHttpCore, akkaHttpExperimental, akkaHttpTestkit, akkaHttpSession, akkaHttpCirce)

val swagger = "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.9.1"

// Slick
val slick         = "com.typesafe.slick" %% "slick" % slickVersion
val slickCodeGen  = "com.typesafe.slick" %% "slick-codegen" % slickVersion
val slickHikari   = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
val h2            = "com.h2database" % "h2" % "1.3.176" //watch out! 1.4.190 is beta
val postgres      = "org.postgresql" % "postgresql" % "9.4.1208"
val flyway        = "org.flywaydb" % "flyway-core" % "4.0"
val slickStack    = Seq(slick, slickCodeGen, h2, postgres, slickHikari, flyway)

val typesafeConfig = "com.typesafe" % "config" % "1.3.1"

val javaxMailSun = "com.sun.mail" % "javax.mail" % "1.6.0"

val scalatest         = "org.scalatest" %% "scalatest" % "3.0.1" % "test"
val unitTestingStack  = Seq(scalatest)

val circeVersion = "0.8.0"

val argon2javaVersion = "2.2"
val argon2java        = "de.mkammerer" % "argon2-jvm" % argon2javaVersion

lazy val rootProject = (project in file("backend"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    commonSettings,
    Revolver.settings,
    RenameProject.settings,
    libraryDependencies ++= unitTestingStack ++ loggingStack ++ akkaStack ++ slickStack ++ Seq(
      javaxMailSun,
      typesafeConfig,
      swagger,
      argon2java,
      "mysql" % "mysql-connector-java" % "6.0.6",
      "org.mindrot" % "jbcrypt" % "0.3m"
    ) ++ Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion),
    resolvers += Resolver.bintrayRepo("hseeberger", "maven"),
    scaladOptions := {
      val out = streams.value
      val log = out.log
      log.info("123")
      val uc = update.value // update task happens-before scalacOptions
      log.info("456")
      uc.allConfigurations.take(3)
    },
    slickGen <<= slickCodeGenTask,
    buildInfoKeys     := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage  := "com.jc.api.endpoint.version",
    buildInfoObject   := "BuildInfo",
    buildInfoKeys     := Seq[BuildInfoKey](
      BuildInfoKey.action("buildDate")(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())),
      // if the build is done outside of a git repository, we still want it to succeed
      BuildInfoKey.action("buildSha")(Try(Process("git rev-parse HEAD").!!.stripLineEnd).getOrElse("?"))
    ),
    mainClass in Compile := Some("com.jc.api.Main")
  )

lazy val ui = (project in file("ui"))
  .settings(commonSettings: _*)
  .settings(test in Test := (test in Test).dependsOn(npmTask.toTask(" run test")).value)


lazy val slickGen = TaskKey[Seq[File]]("gen-tables")
lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  val outputDir = (dir / "slick").getPath // place generated files in sbt's managed sources folder
  val url = "jdbc:postgresql://localhost:5432/account_service" // connection info for a pre-populated throw-away, in-memory db for this demo, which is freshly initialized on every run
  val jdbcDriver = "org.postgresql.Driver"
  val slickDriver = "slick.jdbc.PostgresProfile"
  val pkg = "com.jc.api.schema"
  val userName = "jc_acct"
  val password = "chairmanmao"
  toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg, userName, password), s.log))
  val fname = outputDir + "/com/jc/api/schema/"
  Seq(file(fname))
}