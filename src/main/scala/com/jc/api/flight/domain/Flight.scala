package com.jc.api.flight.domain

import java.time.OffsetDateTime

import com.jc.api.flight.{FlightPlanId, FlightStepId}
import com.jc.api.location.LocationId
import com.jc.api.location.domain.Location
import com.jc.api.order.OrderId
import com.jc.api.user.UserId
import com.jc.api.user.domain.User

case class FlightStep(
  id: FlightStepId,
  planId: FlightPlanId,
  fromLocationId: LocationId,
  toLocationId: LocationId,
  fromTime: OffsetDateTime,
  toTime: OffsetDateTime
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