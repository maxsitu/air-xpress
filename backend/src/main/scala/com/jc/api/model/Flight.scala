package com.jc.api.model

import java.time.OffsetDateTime

import com.jc.api.endpoint.flight.{FlightPlanId, FlightStepId}
import com.jc.api.endpoint.location.LocationId
import com.jc.api.endpoint.order.OrderId
import com.jc.api.endpoint.user.UserId
import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor}

case class FlightStep(
  id            : FlightStepId,
  planId        : FlightPlanId,

  fromLocationId: LocationId,
  toLocationId  : LocationId,
  fromTime      : OffsetDateTime,
  toTime        : OffsetDateTime
)

case class FlightPlan(
  id            : FlightPlanId,
  provideUserId : UserId,
  consumeUserId : UserId,
  initiateUserId: UserId,
  passengerCount: Int,
  startTimed    : OffsetDateTime,
  endTimed      : OffsetDateTime,
  createdOn     : OffsetDateTime,
  modifiedOn    : OffsetDateTime
)

case class FlightOrder(
  id            : OrderId,
  createdOn     : OffsetDateTime,
  confirmedOn   : Option[OffsetDateTime],
  rejectedOn    : Option[OffsetDateTime]
)

case class BasicFlightStep(
  fromLocationId: LocationId,
  toLocationId  : LocationId,
  fromTime      : OffsetDateTime,
  toTime        : OffsetDateTime
) {
  implicit object BasicFlightPlanDecoder extends Decoder[BasicFlightStep] {
    override def apply(c: HCursor): Result[BasicFlightStep] =
      for {
        frmLocId <- c.get[LocationId]("fromLocationId")
        toLocId  <- c.get[LocationId]("toLocationId")
        fromTime <- c.get[OffsetDateTime]("fromTime")
        toTime   <- c.get[OffsetDateTime]("toTime")
      } yield BasicFlightStep(frmLocId, toLocId, fromTime, toTime)
  }
}

case class BasicFlightPlan(

)