package com.jc.api.endpoint.ask.application

import java.time.{Instant, OffsetDateTime, ZoneOffset}

import com.jc.api.common.Utils
import com.jc.api.common.sql.SqlDatabase
import com.jc.api.endpoint.ask.AskId
import com.jc.api.endpoint.ask.api.BasicProviderAskData
import com.jc.api.endpoint.flight.application.SqlFlightSchema
import com.jc.api.endpoint.user.UserId
import com.jc.api.endpoint.user.application.SqlUserSchema
import com.jc.api.model._

import scala.concurrent.{ExecutionContext, Future}

class AskDao (protected val database: SqlDatabase)(implicit val ec: ExecutionContext) extends SqlAskSchema {

  import database._
  import database.profile.api._

  private def findOneProviderAskWhere(condition: ProviderAsks => Rep[Boolean]) = db.run(providerAsks.filter(condition).result.headOption)
  private def findProviderAskWhere(condition: ProviderAsks => Rep[Boolean]) = db.run(providerAsks.filter(condition).result)
  private def findOneConsumerAskWhere(condition: ConsumerAsks => Rep[Boolean]) = db.run(consumerAsks.filter(condition).result.headOption)
  private def findConsumerAskWhere(condition: ConsumerAsks => Rep[Boolean]) = db.run(consumerAsks.filter(condition).result)

  def addProviderAsk(providerAsk: ProviderAsk): Future[AskId] = db.run((providerAsks returning providerAsks.map(_.id)) += providerAsk)
  def addConsumerAsk(consumerAsk: ConsumerAsk): Future[AskId] = db.run((consumerAsks returning consumerAsks.map(_.id)) += consumerAsk)

  def addProviderAsk(providerId: UserId,  providerAsk: BasicProviderAskData): Future[AskId] = {
    val plan = providerAsk.plan

    db.run(
      (
        for {
          planId <- flightPlans returning flightPlans.map(_.id) += FlightPlan(
            0, plan.passengerNum, plan.startTime, plan.endTime, Instant.now().atOffset(ZoneOffset.UTC)
          )
          stepIdList <- flightSteps returning flightSteps.map(_.id) ++= (plan.flightSteps map (step => FlightStep(
            0, planId, step.fromLocationId, step.toLocationId, step.fromTime, step.toTime
          )))
          askId <- providerAsks returning providerAsks.map(_.id) += ProviderAsk(
            0, Some(planId), providerId, providerAsk.seats, providerAsk.price, None,
            Instant.now().atOffset(ZoneOffset.UTC), Instant.now().atOffset(ZoneOffset.UTC)
          )
        } yield askId

        ).transactionally
    )
  }

  def findProviderAsksByProviderId(providerId: UserId): Future[Seq[ProviderAsk]] = findProviderAskWhere(_.providerId === providerId)
  def findConsumerAsksByConsumerId(consumerId: UserId): Future[Seq[ConsumerAsk]] = findConsumerAskWhere(_.consumerId === consumerId)

  def findActiveProviderAsksByProviderId(providerId: UserId): Future[Seq[ProviderAsk]] =
    db.run(
      providerAsks.filter(x =>
        (x.providerId === providerId && x.active)
      ).result
    )

  def findActiveConsumerAsksByConsumerId(consumerId: UserId): Future[Seq[ConsumerAsk]] =
    db.run(
      consumerAsks.filter(x =>
        (x.consumerId === consumerId && x.active)
      ).result
    )

  def findProviderAskByConsuerBid(bid: ConsumerBid): Future[ProviderAsk] =
    db.run(
      providerAsks.filter(x =>
        x.id === bid.providerAskId
      ).result.head
    )

  def findConsumerAskByProviderBid(bid: ProviderBid): Future[ConsumerAsk] =
    db.run(
      consumerAsks.filter(x =>
        x.id === bid.consumerAskId
      ).result.head
    )

  def setProviderAskStatus(askId: AskId, isActive: Boolean): Future[Unit] =
    db.run(
      providerAsks.filter(_.id === askId)
        .map(a => (a.active, a.modifiedOn))
        .update((Some(isActive), Utils.now()))
    ).map(_ => ())

  def setConsumerAskStatus(askId: AskId, isActive: Boolean): Future[Unit] =
    db.run(
      consumerAsks.filter(_.id === askId)
        .map(a => (a.active, a.modifiedOn))
        .update((Some(isActive), Utils.now()))
    ).map(_ => ())

  def countProviderAsksByProviderId(providerId: UserId): Future[Int] =
    db.run(
      providerAsks.filter(_.providerId === providerId).length.result
    )

  def countConsumerAsksByConsumerId(consumerId: UserId): Future[Int] =
    db.run(
      consumerAsks.filter(_.consumerId === consumerId).length.result
    )


  def rangeProviderAsks(providerId: UserId, offset: Int, limit: Int): Future[Seq[ProviderAsk]] =
    db.run(
      providerAsks.filter(_.providerId === providerId)
        .drop(offset)
        .take(limit).result
    )

  def rangeConsumerAsks(consumerId: UserId, offset: Int, limit: Int): Future[Seq[ConsumerAsk]] =
    db.run(
      consumerAsks.filter(_.consumerId === consumerId)
        .drop(offset)
        .take(limit).result
    )
}

trait SqlAskSchema extends SqlFlightSchema with SqlUserSchema {
  protected val database: SqlDatabase

  import database._
  import database.profile.api._
  import slick.jdbc.{GetResult => GR}
  import slick.model.ForeignKeyAction

  /** GetResult implicit for fetching ProviderAsksRow objects using plain SQL queries */
  implicit def GetResultProviderAsksRow(
    implicit e0: GR[Long],
    e1: GR[Option[Long]],
    e2: GR[java.util.UUID],
    e3: GR[Int], e4: GR[Double],
    e5: GR[Option[Boolean]],
    e6: GR[OffsetDateTime]
  ): GR[ProviderAsk] = GR { prs =>
    import prs._
    ProviderAsk.tupled(
      (<<[Long], <<?[Long], <<[java.util.UUID], <<[Int], <<[Double], <<?[Boolean], <<[OffsetDateTime], <<[OffsetDateTime])
    )
  }

  /** Table description of table PROVIDER_ASKS. Objects of this class serve as prototypes for rows in queries. */
  class ProviderAsks(_tableTag: Tag) extends profile.api.Table[ProviderAsk](_tableTag, "PROVIDER_ASKS") {
    def * = (id, planId, providerId, seats, price, active, createdOn, modifiedOn) <> (ProviderAsk.tupled, ProviderAsk.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), planId, Rep.Some(providerId), Rep.Some(seats), Rep.Some(price), active, Rep.Some(createdOn
    ), Rep.Some(modifiedOn)).shaped.<>(
      { r => import r._; _1.map(_ => ProviderAsk.tupled((_1.get, _2, _3.get, _4.get, _5.get, _6, _7.get, _8.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column plan_id SqlType(int8), Default(None) */
    val planId: Rep[Option[Long]] = column[Option[Long]]("plan_id", O.Default(None))
    /** Database column provider_id SqlType(uuid) */
    val providerId: Rep[java.util.UUID] = column[java.util.UUID]("provider_id")
    /** Database column seats SqlType(int4) */
    val seats: Rep[Int] = column[Int]("seats")
    /** Database column price SqlType(float8) */
    val price: Rep[Double] = column[Double]("price")
    /** Database column active SqlType(bool), Default(Some(true)) */
    val active: Rep[Option[Boolean]] = column[Option[Boolean]]("active", O.Default(Some(true)))
    /** Database column created_on SqlType(OffsetDateTime) */
    val createdOn: Rep[OffsetDateTime] = column[OffsetDateTime]("created_on")
    /** Database column modified_on SqlType(OffsetDateTime) */
    val modifiedOn: Rep[OffsetDateTime] = column[OffsetDateTime]("modified_on")

    /** Foreign key referencing FlightPlans (database name PROVIDER_ASKS_plan_id_fkey) */
    lazy val flightPlansFk = foreignKey("PROVIDER_ASKS_plan_id_fkey", planId, flightPlans)(
      r => Rep.Some(r.id), onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
    /** Foreign key referencing Users (database name PROVIDER_ASKS_provider_id_fkey) */
    lazy val usersFk = foreignKey("PROVIDER_ASKS_provider_id_fkey", providerId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table ProviderAsks */
  lazy val providerAsks = new TableQuery(tag => new ProviderAsks(tag))

  /** GetResult implicit for fetching ConsumerAsksRow objects using plain SQL queries */
  implicit def GetResultConsumerAsksRow(
    implicit e0: GR[Long],
    e1: GR[Option[Long]],
    e2: GR[java.util.UUID],
    e3: GR[Int],
    e4: GR[Option[Boolean]],
    e5: GR[OffsetDateTime]): GR[ConsumerAsk] = GR
  {
    prs =>
      import prs._
      ConsumerAsk.tupled(
        (<<[Long], <<?[Long], <<[java.util.UUID], <<[Int], <<?[Boolean], <<[OffsetDateTime], <<[OffsetDateTime])
      )
  }

  /** Table description of table CONSUMER_ASKS. Objects of this class serve as prototypes for rows in queries. */
  class ConsumerAsks(_tableTag: Tag) extends profile.api.Table[ConsumerAsk](_tableTag, "CONSUMER_ASKS") {
    def * = (id, planId, consumerId, passengers, active, askedOn, modifiedOn) <> (ConsumerAsk.tupled, ConsumerAsk.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), planId, Rep.Some(consumerId), Rep.Some(passengers), active, Rep.Some(askedOn), Rep.Some(
      modifiedOn
    )).shaped.<>(
      { r => import r._; _1.map(_ => ConsumerAsk.tupled((_1.get, _2, _3.get, _4.get, _5, _6.get, _7.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column plan_id SqlType(int8), Default(None) */
    val planId: Rep[Option[Long]] = column[Option[Long]]("plan_id", O.Default(None))
    /** Database column consumer_id SqlType(uuid) */
    val consumerId: Rep[java.util.UUID] = column[java.util.UUID]("consumer_id")
    /** Database column passengers SqlType(int8) */
    val passengers: Rep[Int] = column[Int]("passengers")
    /** Database column active SqlType(bool), Default(Some(true)) */
    val active: Rep[Option[Boolean]] = column[Option[Boolean]]("active", O.Default(Some(true)))
    /** Database column asked_on SqlType(OffsetDateTime) */
    val askedOn: Rep[OffsetDateTime] = column[OffsetDateTime]("asked_on")
    /** Database column modified_on SqlType(OffsetDateTime) */
    val modifiedOn: Rep[OffsetDateTime] = column[OffsetDateTime]("modified_on")

    /** Foreign key referencing FlightPlans (database name CONSUMER_ASKS_plan_id_fkey) */
    lazy val flightPlansFk = foreignKey("CONSUMER_ASKS_plan_id_fkey", planId, flightPlans)(
      r => Rep.Some(r.id), onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
    /** Foreign key referencing Users (database name CONSUMER_ASKS_consumer_id_fkey) */
    lazy val usersFk = foreignKey("CONSUMER_ASKS_consumer_id_fkey", consumerId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table ConsumerAsks */
  lazy val consumerAsks = new TableQuery(tag => new ConsumerAsks(tag))
}