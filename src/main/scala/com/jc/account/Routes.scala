package com.jc.account

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import com.jc.account.common.api.RoutesRequestWrapper
import com.jc.account.swagger.SwaggerDocService
import com.jc.account.user.api.UsersRoutes

trait Routes extends RoutesRequestWrapper with UsersRoutes {

  def system: ActorSystem
  def config: ServerConfig

  lazy val routes = requestWrapper {
    pathPrefix("api") {
        usersRoutes
    } ~
      getFromResourceDirectory("webapp") ~
      new SwaggerDocService(config.serverHost, config.serverPort, system).routes ~
      path("") {
        getFromResource("webapp/index.html")
      }
  }
}
