package com.jc.api.endpoint.flight.api

import akka.http.scaladsl.server.Directives._
import com.jc.api.common.api.RoutesSupport
import com.jc.api.endpoint.flight.application.FlightService
import com.jc.api.endpoint.user.api.SessionSupport
import com.typesafe.scalalogging.StrictLogging

trait FlightRoutes extends RoutesSupport with StrictLogging with SessionSupport{
  def flightService: FlightService

  val flightROutes = pathPrefix("flightPlan") {
    put {
      completeOk
    }
  } ~ pathPrefix("flightStep") {
    put {
      completeOk
    }
  } ~ pathPrefix("flightOrder") {
    put {
      completeOk
    }
  }
}

case class FlightPlanInput ()