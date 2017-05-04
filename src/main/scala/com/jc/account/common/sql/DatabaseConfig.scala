package com.jc.account.common.sql

import com.jc.account.common.ConfigWithDefault
import DatabaseConfig._
import com.typesafe.config.Config

trait DatabaseConfig extends ConfigWithDefault {
  def rootConfig: Config

  // format: OFF
  lazy val dbH2Url              = getString(s"jc.account.db.h2.properties.url", "jdbc:h2:file:./data/jc.account")
  lazy val dbPostgresServerName = getString(PostgresServerNameKey, "")
  lazy val dbPostgresPort       = getString(PostgresPortKey, "5432")
  lazy val dbPostgresDbName     = getString(PostgresDbNameKey, "")
  lazy val dbPostgresUsername   = getString(PostgresUsernameKey, "")
  lazy val dbPostgresPassword   = getString(PostgresPasswordKey, "")
}

object DatabaseConfig {
  val PostgresDSClass       = "jc.account.db.postgres.dataSourceClass"
  val PostgresServerNameKey = "jc.account.db.postgres.properties.serverName"
  val PostgresPortKey       = "jc.account.db.postgres.properties.portNumber"
  val PostgresDbNameKey     = "jc.account.db.postgres.properties.databaseName"
  val PostgresUsernameKey   = "jc.account.db.postgres.properties.user"
  val PostgresPasswordKey   = "jc.account.db.postgres.properties.password"
  // format: ON
}
