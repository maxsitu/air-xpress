package com.jc.api.order.api

import java.time.OffsetDateTime

import com.typesafe.scalalogging.StrictLogging
import akka.http.scaladsl.server.Directives._
import com.jc.api.common.api.RoutesSupport
import com.jc.api.order.OrderId
import com.jc.api.order.application.OrderService
import com.jc.api.order.domain.Order
import com.jc.api.user.UserId
import com.jc.api.user.api.SessionSupport
import io.circe.generic.auto._

/**
  * Created by walle on 7/10/17.
  */
trait OrdersRoutes extends RoutesSupport with StrictLogging with SessionSupport{

  def orderService: OrderService

  implicit val orderDataCbs = CanBeSerialized[Order]

  val ordersRoutes = pathPrefix("orders") {
    put {
      userFromSession { _ =>
        entity(as[OrderInput]) { orderInput =>
          onSuccess(orderService.add(orderInput.asOrder)) { _ =>
            completeOk
          }
        }
      }
    } ~
    path("byRequestUser") {
      get {
        userFromSession { user =>
          onSuccess(orderService.findByRequestUserId(user.id)) { orders =>
            complete(orders)
          }
        }
      }
    } ~
    path("byAcceptUser" / JavaUUID) { acceptUserId =>
      get {
        userFromSession { user =>
          onSuccess(orderService.findByRequestUserId(acceptUserId)) { orders =>
            complete(orders)
          }
        }
      }
    }
  }
}

case class OrderInput (
  requestUserId:  UserId,
  acceptUserId:   UserId,
  eventStartsOn:  OffsetDateTime,
  eventEndsOn:    OffsetDateTime,
  fromLocationId: Long,
  toLocationId:   Long,
  planId:         Long,
  createdOn:      OffsetDateTime
) {
  def asOrder = Order(Some(0), requestUserId, acceptUserId, eventStartsOn, eventEndsOn, fromLocationId, toLocationId, planId, createdOn)
}

