package com.jc.api

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import com.jc.api.common.api.RoutesRequestWrapper
import com.jc.api.endpoint.bid.api.BidsRoutes
import com.jc.api.endpoint.location.api.LocationRoutes
import com.jc.api.endpoint.order.api.OrdersRoutes
import com.jc.api.endpoint.plane.api.PlanesRoutes
import com.jc.api.endpoint.swagger.SwaggerDocService
import com.jc.api.endpoint.user.api.UsersRoutes
import com.jc.api.endpoint.version.VersionRoutes

trait Routes extends RoutesRequestWrapper
  with UsersRoutes
  with OrdersRoutes
  with LocationRoutes
  with PlanesRoutes
  with BidsRoutes
  with VersionRoutes{

  def system: ActorSystem
  def config: ServerConfig

  lazy val routes = requestWrapper {
    pathPrefix("api") {
        usersRoutes ~
        ordersRoutes ~
        locationRoutes ~
        planesRoutes ~
        bidsRoutes ~
        versionRoutes
    } ~
      getFromResourceDirectory("webapp") ~
      new SwaggerDocService(config.serverHost, config.serverPort, system).routes ~
      path("") {
        getFromResource("webapp/index.html")
      }
  }
}
