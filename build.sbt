import java.text.SimpleDateFormat
import java.util.Date

import sbt.Keys.version

import scala.sys.process.Process
import scala.util.Try
import complete.DefaultParsers._
import sbt.{CrossVersion, IO}

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
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq(scalaVersion.value, "2.11.8"),
  crossVersion := CrossVersion.binary,
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

lazy val slickGen = TaskKey[Seq[File]]("gen-tables")

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "air-xpress",
    herokuFatJar in Compile := Some((assemblyOutputPath in backend in assembly).value),
    deployHeroku in Compile := ((deployHeroku in Compile) dependsOn (assembly in backend)).value
  )
  .aggregate(backend, ui)

lazy val backend = (project in file("backend"))
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
    slickGen := {
      val outputDir = (sourceManaged.value / "slick").getPath
      val url = "jdbc:postgresql://localhost:5432/account_service" //
      val jdbcDriver = "org.postgresql.Driver"
      val slickDriver = "slick.jdbc.PostgresProfile"
      val pkg = "com.jc.api.schema"
      val userName = "jc_acct"
      val password = "chairmanmao"
      (runner in Compile).value.run("slick.codegen.SourceCodeGenerator", (dependencyClasspath in Compile).value.files, Seq[String](slickDriver, jdbcDriver, url, outputDir, pkg, userName, password), streams.value.log)

      val fname = outputDir + "/com/jc/api/schema/"
      Seq(file(fname))
    },
    buildInfoKeys     := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage  := "com.jc.api.endpoint.version",
    buildInfoObject   := "BuildInfo",
    buildInfoKeys     := Seq[BuildInfoKey](
      BuildInfoKey.action("buildDate")(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())),
      // if the build is done outside of a git repository, we still want it to succeed
      BuildInfoKey.action("buildSha")(Try(Process("git rev-parse HEAD").!!.stripLineEnd).getOrElse("?"))
    ),
    compile in Compile := {
      val compilationResult = (compile in Compile).value
      IO.touch(target.value / "compilationFinished")

      compilationResult
    },
    mainClass in Compile := Some("com.jc.api.Main"),
    unmanagedResourceDirectories in Compile := {
      (unmanagedResourceDirectories in Compile).value ++ List(
        baseDirectory.value.getParentFile / ui.base.getName / "dist"
      )
    }
  )

lazy val ui = (project in file("ui"))
  .settings(commonSettings: _*)
  .settings(test in Test := (test in Test).dependsOn(npmTask.toTask(" run test")).value
  )


