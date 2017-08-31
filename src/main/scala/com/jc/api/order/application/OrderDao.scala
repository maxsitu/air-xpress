package com.jc.api.order.application

import java.time.OffsetDateTime
import java.util.UUID

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.flight.FlightPlanId
import com.jc.api.location.application.LocationDao
import com.jc.api.order.OrderId
import com.jc.api.order.domain.Order

import scala.concurrent.{ExecutionContext, Future}

class OrderDao(protected val database: SqlDatabase)(implicit val ec: ExecutionContext) extends SqlOrderSchema {
  import database._
  import database.driver.api._

  def add(order: Order): Future[OrderId] = db.run((orders returning orders.map(_.id)) += order)

  def findById(orderId: OrderId): Future[Option[Order]] =
    db.run(orders.filter(_.id === orderId).result.headOption)
}

trait SqlOrderSchema {
  protected val database: SqlDatabase
  import database._
  import database.driver.api._

  protected val orders = TableQuery[Orders]

  protected class Orders(tag: Tag) extends Table[Order](tag, "orders") {
    def id              = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def createdOn       = column[OffsetDateTime]("created_on")
    def confirmedOn     = column[Option[OffsetDateTime]]("confirmed_on")
    def rejectedOn      = column[Option[OffsetDateTime]]("rejected_on")

    def * = (id, createdOn, confirmedOn, rejectedOn) <> (Order.tupled, Order.unapply)
  }
}