package com.jc.api.order.application

import java.time.OffsetDateTime
import java.util.UUID

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.order.OrderId
import com.jc.api.order.domain.Order

import scala.concurrent.{ExecutionContext, Future}

class OrderDao(protected val database: SqlDatabase)(implicit val ec: ExecutionContext) extends SqlOrderSchema {
  import database._
  import database.driver.api._

  def add(order: Order): Future[OrderId] = db.run((orders returning orders.map(_.id)) += order)

  def findById(orderId: OrderId): Future[Option[Order]] =
    db.run(orders.filter(_.id === orderId).result.headOption)

  def findByRequestUserId(reqestUserId: UUID): Future[Seq[Order]] =
    db.run(orders.filter(_.requestUserId === reqestUserId).result)

  def findByAcceptUserId(acceptUserId: UUID): Future[Seq[Order]] =
    db.run(orders.filter(_.acceptUserId === acceptUserId).result)
}

trait SqlOrderSchema {
  protected val database: SqlDatabase
  import database._
  import database.driver.api._

  protected val orders = TableQuery[Orders]

  protected class Orders(tag: Tag) extends Table[Order](tag, "orders") {
    def id              = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def requestUserId   = column[UUID]("request_user_id")
    def acceptUserId    = column[UUID]("accept_user_id")
    def eventStartsOn   = column[OffsetDateTime]("event_starts_on")
    def eventEndsOn     = column[OffsetDateTime]("event_ends_on")
    def fromLocationId  = column[Long]("from_location_id")
    def toLocationId    = column[Long]("from_location_id")
    def planId          = column[Long]("plan_id")
    def createdOn       = column[OffsetDateTime]("created_on")

    def * = (id.?, requestUserId, acceptUserId, eventStartsOn, eventEndsOn, fromLocationId, toLocationId, planId, createdOn) <> (Order.tupled, Order.unapply _)
  }
}