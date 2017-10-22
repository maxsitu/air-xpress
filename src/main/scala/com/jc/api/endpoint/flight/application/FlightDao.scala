package com.jc.api.endpoint.flight.application

import java.time.OffsetDateTime

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.endpoint.flight.{FlightOrderId, FlightPlanId, FlightStepId}
import com.jc.api.model.{FlightOrder, FlightPlan}
import com.jc.api.endpoint.location.LocationId
import com.jc.api.endpoint.order.OrderId
import com.jc.api.endpoint.user.UserId
import com.jc.api.model.{FlightOrder, FlightPlan, FlightStep}

import scala.concurrent.{ExecutionContext, Future}

class FlightDao (protected val database: SqlDatabase)(implicit val ec: ExecutionContext) extends SqlFlightSchema {
  import database._
  import database.profile.api._

  def addPlan(plan: FlightPlan): Future[FlightPlanId] =
    db.run((flightPlans returning flightPlans.map(_.id)) += plan)


  def findPlanByProvideUserId(providerId: UserId): Future[Seq[FlightPlan]] =
    db.run(flightPlans.filter(_.provideUserId === providerId).result)

  def findPlanByConsumeUserId(consumerId: UserId): Future[Seq[FlightPlan]] =
    db.run(flightPlans.filter(_.consumeUserId === consumerId).result)

  def findPlanByInitiateUserId(initiateUserId: UserId): Future[Seq[FlightPlan]] =
    db.run(flightPlans.filter(_.initiateUserId === initiateUserId).result)

  def addStep(step: FlightStep): Future[FlightStepId] =
    db.run((flightSteps returning flightSteps.map(_.id)) += step)

  def findStepByPlanId(planId: FlightPlanId): Future[Seq[FlightStep]] =
    db.run(flightSteps.filter(_.planId === planId).result)

  def findStepByPlanIdSortAscByFromTime(planId: FlightPlanId): Future[Seq[FlightStep]] =
    db.run(flightSteps.filter(_.planId === planId).sortBy(_.fromTime.asc).result)

  def addOrder(order: FlightOrder): Future[FlightOrderId] =
    db.run((flightOrders returning flightOrders.map(_.id)) += order)

  def findOrderByPlanId(planId: FlightPlanId): Future[Option[FlightOrder]] =
    db.run(
      (for {
          orderId <- flightPlans.filter(_.id === planId).map(_.orderId)
          order <- flightOrders.filter(_.id === orderId) if orderId.isDefined
        }
          yield order)
        .result.headOption
    )
}

trait SqlFlightSchema {
  protected val database: SqlDatabase

  import database._
  import database.profile.api._

  protected val flightPlans = TableQuery[FlightPlans]

  protected val flightSteps = TableQuery[FlightSteps]

  protected val flightOrders = TableQuery[FlightOrders]

  protected class FlightPlans(tag: Tag) extends Table[FlightPlan](tag, "flight_plans") {
    def id          = column[FlightPlanId]("id", O.PrimaryKey, O.AutoInc)
    def orderId     = column[Option[OrderId]]("order_id")
    def provideUserId  = column[UserId]("provide_user_id")
    def consumeUserId  = column[UserId]("consume_user_id")
    def initiateUserId = column[UserId]("initiate_user_id")
    def passengerCount = column[Int]("passenger_count")
    def startTime   = column[OffsetDateTime]("start_time")
    def endTime     = column[OffsetDateTime]("end_time")
    def createdOn   = column[OffsetDateTime]("created_on")
    def modifiedOn  = column[OffsetDateTime]("modified_on")

    def * = (id, orderId, provideUserId, consumeUserId, initiateUserId, passengerCount, startTime, endTime, createdOn, modifiedOn) <> (FlightPlan.tupled, FlightPlan.unapply)
  }

  protected class FlightSteps(tag: Tag) extends Table[FlightStep](tag, "flight_steps") {
    def id          = column[FlightStepId]("id", O.PrimaryKey, O.AutoInc)
    def planId      = column[FlightPlanId]("plan_id")
    def fromLocationId  = column[LocationId]("from_location_id")
    def toLocationId    = column[LocationId]("to_location_id")
    def fromTime        = column[OffsetDateTime]("from_time")
    def toTime          = column[OffsetDateTime]("to_time")

    def * = (id, planId, fromLocationId, toLocationId, fromTime, toTime) <> (FlightStep.tupled, FlightStep.unapply)
  }

  protected class FlightOrders(tag: Tag) extends Table[FlightOrder](tag, "flight_orders") {
    def id              = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def createdOn       = column[OffsetDateTime]("created_on")
    def confirmedOn     = column[Option[OffsetDateTime]]("confirmed_on")
    def rejectedOn      = column[Option[OffsetDateTime]]("rejected_on")

    def * = (id, createdOn, confirmedOn, rejectedOn) <> (FlightOrder.tupled, FlightOrder.unapply)
  }
}