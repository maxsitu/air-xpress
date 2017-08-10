package com.jc.api.order.domain

import java.time.OffsetDateTime

import com.jc.api.order.OrderId
import com.jc.api.user.UserId

case class Order(
  id            : OrderId,
  createdOn     : OffsetDateTime,
  confirmedOn   : Option[OffsetDateTime],
  rejectedOn    : Option[OffsetDateTime]
)

//case class BasicOrderData(id: OrderId, )