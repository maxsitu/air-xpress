package com.jc.api

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import com.jc.api.common.api.RoutesRequestWrapper
import com.jc.api.location.api.LocationRoutes
import com.jc.api.order.api.OrdersRoutes
import com.jc.api.plane.api.PlanesRoutes
import com.jc.api.swagger.SwaggerDocService
import com.jc.api.user.api.UsersRoutes

trait Routes extends RoutesRequestWrapper
  with UsersRoutes
  with OrdersRoutes
  with LocationRoutes
  with PlanesRoutes {

  def system: ActorSystem
  def config: ServerConfig

  lazy val routes = requestWrapper {
    pathPrefix("api") {
        usersRoutes ~
        ordersRoutes ~
        locationRoutes ~
        planesRoutes
    } ~
      getFromResourceDirectory("webapp") ~
      new SwaggerDocService(config.serverHost, config.serverPort, system).routes ~
      path("") {
        getFromResource("webapp/index.html")
      }
  }
}
