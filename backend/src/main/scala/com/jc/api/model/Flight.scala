package com.jc.api.model

import java.time.OffsetDateTime

import com.jc.api.endpoint.flight.{FlightPlanId, FlightStepId}
import com.jc.api.endpoint.location.LocationId
import com.jc.api.endpoint.order.OrderId

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
  passengerNum  : Int,
  startTime     : OffsetDateTime,
  endTime       : OffsetDateTime,
  modifiedOn    : OffsetDateTime
)

case class FlightOrder(
  id            : OrderId,
  createdOn     : OffsetDateTime,
  confirmedOn   : Option[OffsetDateTime],
  rejectedOn    : Option[OffsetDateTime]
)