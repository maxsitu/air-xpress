package com.jc.api.model

import java.time.OffsetDateTime

import com.jc.api.endpoint.flight.{FlightPlanId, FlightStepId}
import com.jc.api.endpoint.location.LocationId
import com.jc.api.endpoint.order.OrderId
import com.jc.api.endpoint.user.UserId

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
  orderId       : Option[OrderId],
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