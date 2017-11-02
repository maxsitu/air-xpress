package com.jc.api.model

import java.time.OffsetDateTime

import com.jc.api.endpoint.order.OrderId

case class Order(
  id            : OrderId,
  createdOn     : OffsetDateTime,
  confirmedOn   : Option[OffsetDateTime],
  rejectedOn    : Option[OffsetDateTime]
)

//case class BasicOrderData(id: OrderId, )