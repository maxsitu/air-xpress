package com.jc.api.order.domain

import java.time.OffsetDateTime

import com.jc.api.order.OrderId
import com.jc.api.user.UserId

case class Order (
    id: Option[OrderId],
    requestUserId: UserId,
    acceptUserId: UserId,
    eventStartsOn: OffsetDateTime,
    eventEndsOn: OffsetDateTime,
    fromLocationId: Long,
    toLocationId: Long,
    planId: Long,
    createdOn: OffsetDateTime
)

//case class BasicOrderData(id: OrderId, )