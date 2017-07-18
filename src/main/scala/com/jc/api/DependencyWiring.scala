package com.jc.api

import akka.actor.ActorSystem
import com.jc.api.common.sql.{DatabaseConfig, SqlDatabase}
import com.jc.api.location.application.{LocationDao, LocationService}
import com.jc.api.order.application.{OrderDao, OrderService}
import com.jc.api.user.application.{RefreshTokenStorageImpl, RememberMeTokenDao, UserDao, UserService}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging

trait DependencyWiring extends StrictLogging {
  def system: ActorSystem

  lazy val config = new DatabaseConfig with ServerConfig {
    override def rootConfig = ConfigFactory.load()
  }

  lazy val sqlDatabase = SqlDatabase.create(config)

  lazy val daoExecutionContext = system.dispatchers.lookup("dao-dispatcher")

  lazy val userDao = new UserDao(sqlDatabase)(daoExecutionContext)

  lazy val orderDao = new OrderDao(sqlDatabase)(daoExecutionContext)

  lazy val locationDao = new LocationDao(sqlDatabase)(daoExecutionContext)

  lazy val rememberMeTokenDao = new RememberMeTokenDao(sqlDatabase)(daoExecutionContext)

  lazy val serviceExecutionContext = system.dispatchers.lookup("service-dispatcher")


  lazy val userService = new UserService(
    userDao
  )(serviceExecutionContext)

  lazy val orderService = new OrderService(
    orderDao
  )(serviceExecutionContext)

  lazy val locationService = new LocationService(
    locationDao
  )(serviceExecutionContext)

  lazy val refreshTokenStorage = new RefreshTokenStorageImpl(rememberMeTokenDao, system)(serviceExecutionContext)
}
