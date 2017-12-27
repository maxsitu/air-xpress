package com.jc.api.endpoint.flight.api

import java.time.OffsetDateTime

import akka.http.scaladsl.server.Directives._
import com.jc.api.common.api.RoutesSupport
import com.jc.api.endpoint.flight.application.FlightService
import com.jc.api.endpoint.location.LocationId
import com.jc.api.endpoint.user.api.SessionSupport
import com.typesafe.scalalogging.StrictLogging
import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor}
import io.circe.generic.auto._

trait FlightRoutes extends RoutesSupport with StrictLogging with SessionSupport{
  def flightService: FlightService

  implicit val basicFlightPlanCb = CanBeSerialized[BasicFlightPlan]
  implicit val basicFlightStepCb = CanBeSerialized[BasicFlightStep]

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

case class BasicFlightStep(
  fromLocationId: LocationId,
  toLocationId  : LocationId,
  fromTime      : OffsetDateTime,
  toTime        : OffsetDateTime
) {

}

case class BasicFlightPlan(
  passengerNum  : Int,
  startTime     : OffsetDateTime,
  endTime       : OffsetDateTime,
  flightSteps   : List[BasicFlightStep]
)