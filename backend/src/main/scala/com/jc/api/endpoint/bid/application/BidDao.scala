package com.jc.api.endpoint.bid.application

import java.time.OffsetDateTime

import com.jc.api.common.Utils
import com.jc.api.common.sql.SqlDatabase
import com.jc.api.endpoint.ask.AskId
import com.jc.api.endpoint.ask.application.SqlAskSchema
import com.jc.api.endpoint.bid.BidId
import com.jc.api.endpoint.user.UserId
import com.jc.api.endpoint.user.application.SqlUserSchema
import com.jc.api.model.{ConsumerBid, ProviderAsk, ProviderBid}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class BidDao (protected val database: SqlDatabase)(implicit val ec: ExecutionContext) extends SqlBidSchema {

  import database._
  import database.profile.api._

  private def findOneProviderBidWhere(condition: ProviderBids => Rep[Boolean]) = db.run(providerBids.filter(condition).result.head)
  private def findProviderBidWhere(condition: ProviderBids => Rep[Boolean]) = db.run(providerBids.filter(condition).result)
  private def findOneConsumerBidWhere(condition: ConsumerBids => Rep[Boolean]) = db.run(consumerBids.filter(condition).result.head)
  private def findConsumerBidWhere(condition: ConsumerBids => Rep[Boolean]) = db.run(consumerBids.filter(condition).result)

  def addProviderBid(providerBid: ProviderBid): Future[BidId] = db.run((providerBids returning providerBids.map(_.id)) += providerBid)
  def addConsumerBid(consumerBid: ConsumerBid): Future[BidId] = db.run((consumerBids returning consumerBids.map(_.id)) += consumerBid)

  def findProviderBidById(id: BidId): Future[ProviderBid] = findOneProviderBidWhere(_.id === id)
  def findConsumerBidById(id: BidId): Future[ConsumerBid] = findOneConsumerBidWhere(_.id === id)

  def findProviderBidsByBiderId(bidderId: UserId): Future[Seq[ProviderBid]] = findProviderBidWhere(_.bidderId === bidderId)
  def findConsumerBidsByBiderId(bidderId: UserId): Future[Seq[ConsumerBid]] = findConsumerBidWhere(_.bidderId === bidderId)

  def findProviderBidsByConsumerAskId(consumerAskId: AskId): Future[Seq[ProviderBid]] = findProviderBidWhere(_.consumerAskId === consumerAskId)
  def findConsumerBidsByProviderAskId(providerAskId: AskId): Future[Seq[ConsumerBid]] = findConsumerBidWhere(_.providerAskId === providerAskId)

  def findUnconfirmedProviderBidsByConsumerAskId(consumerAskId: AskId): Future[Seq[ProviderBid]] =
    findProviderBidWhere(bid => bid.consumerAskId === consumerAskId && bid.confirmed === false)
  def findUnconfirmedConsumerBidsByProviderAskId(providerAskId: AskId): Future[Seq[ConsumerBid]] =
    findConsumerBidWhere(bid => bid.providerAskId === providerAskId && bid.confirmed === false)

  def findConfirmedProviderBidsByConsumerAskId(consumerAskId: AskId): Future[Seq[ProviderBid]] =
    findProviderBidWhere(bid => bid.consumerAskId === consumerAskId && bid.confirmed === false)
  def findConfirmedConsumerBidsByProviderAskId(providerAskId: AskId): Future[Seq[ConsumerBid]] =
    findConsumerBidWhere(bid => bid.providerAskId === providerAskId && bid.confirmed === false)

  def setProviderBidStatus(bidId: BidId, isActive: Boolean): Future[Unit] =
    db.run(providerAsks.filter(_.id === bidId).map(b => (b.active, b.modifiedOn)).update((isActive, Utils.now()))).map(_ => ())
  def setConsumerBidStatus(bidId: BidId, isActive: Boolean): Future[Unit] =
    db.run(consumerAsks.filter(_.id === bidId).map(b => (b.active, b.modifiedOn)).update((Some(isActive), Utils.now()))).map(_ => ())

  def confirmProviderBid(bidId: BidId): Future[Unit] =
    db.run(providerBids.filter(_.id === bidId).map(b => (b.confirmed, b.modifiedOn)).update((true, Utils.now())).map(_ => ()))
  def confirmConsumerBid(bidId: BidId): Future[Unit] =
    db.run(consumerBids.filter(_.id === bidId).map(b => (b.confirmed, b.modifiedOn)).update((true, Utils.now())).map(_ => ()))

  def confirmConsumerBidTransactionally(bidId: BidId): Future[Int] = {
    val a =
      (for {
          bid <- consumerBids.filter(_.id === bidId).result.head
          ask <- providerAsks.filter(_.id === bid.providerAskId).result.head
          confirmedSeats <- consumerBids.filter(x => x.confirmed === true && x.providerAskId === ask.id)
            .map(_.passengers).sum.result

        } yield (ask, bid, confirmedSeats)).flatMap {
        case (ask: ProviderAsk, bid: ConsumerBid, confirmedSeats: Option[Int])  =>
          val availableSeats = ask.seats - confirmedSeats.getOrElse(0)
          if (availableSeats < bid.passengers) {
            DBIO.failed(new Exception("Inadequate seats"))
          } else {
            consumerBids.filter(_.id === bidId).map(_.confirmed).update(true)
          }

        case _ => DBIO.failed(new Exception("Invalid result"))
      }.transactionally

    db.run(a)
  }

  def countConfirmedPassengersByProviderAskId(askId: AskId): Future[Int] = {
    val query = consumerBids.filter(bid => bid.confirmed === true).filter(_.providerAskId === askId).map(_.passengers).sum

    db.run(query.result).map(cntOpt => cntOpt.getOrElse(0))
  }

  /**
    * Charging functions
    */
//  def chargeConsumerBidTransactionally(bidId: BidId): Future[]
}


trait SqlBidSchema extends SqlUserSchema with SqlAskSchema {
  protected val database: SqlDatabase

  import database._
  import database.profile.api._
  import slick.jdbc.{GetResult => GR}
  import slick.model.ForeignKeyAction

  /** GetResult implicit for fetching ProviderBidsRow objects using plain SQL queries */
  implicit def GetResultProviderBidsRow(
    implicit e0: GR[Long],
    e1: GR[java.util.UUID],
    e2: GR[Option[Boolean]],
    e3: GR[OffsetDateTime]
  ): GR[ProviderBid] = GR { prs =>
    import prs._
    ProviderBid.tupled(
      (<<[Long], <<[Long], <<[java.util.UUID], <<[Double], <<?[Boolean], <<[Boolean], <<[Boolean], <<[Boolean], <<[OffsetDateTime], <<[OffsetDateTime])
    )
  }

  /** Table description of table PROVIDER_BIDS. Objects of this class serve as prototypes for rows in queries. */
  class ProviderBids(_tableTag: Tag) extends profile.api.Table[ProviderBid](_tableTag, "PROVIDER_BIDS") {
    def * = (id, consumerAskId, bidderId, price, active, confirmed, charged, refunded, createdOn, modifiedOn) <> (ProviderBid.tupled, ProviderBid.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(consumerAskId), Rep.Some(bidderId
    ), price, active, confirmed, charged, refunded, Rep.Some(createdOn), Rep.Some(modifiedOn)).shaped.<>(
      { r => import r._; _1.map(_ => ProviderBid.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7, _8, _9.get, _10.get))
      )
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column consumer_ask_id SqlType(int8) */
    val consumerAskId: Rep[Long] = column[Long]("consumer_ask_id")
    /** Database column bidder_id SqlType(uuid) */
    val bidderId: Rep[java.util.UUID] = column[java.util.UUID]("bidder_id")
    /** Database column price SqlType(float8) */
    val price: Rep[Double] = column[Double]("price")
    /** Database column active SqlType(bool), Default(Some(true)) */
    val active: Rep[Option[Boolean]] = column[Option[Boolean]]("active", O.Default(Some(true)))
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val confirmed: Rep[Boolean] = column[Boolean]("confirmed", O.Default(false))
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val charged: Rep[Boolean] = column[Boolean]("charged", O.Default(false))
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val refunded: Rep[Boolean] = column[Boolean]("refunded", O.Default(false))
    /** Database column created_on SqlType(OffsetDateTime) */
    val createdOn: Rep[OffsetDateTime] = column[OffsetDateTime]("created_on")
    /** Database column modified_on SqlType(OffsetDateTime) */
    val modifiedOn: Rep[OffsetDateTime] = column[OffsetDateTime]("modified_on")

    /** Foreign key referencing ConsumerAsks (database name PROVIDER_BIDS_consumer_ask_id_fkey) */
    lazy val consumerAsksFk = foreignKey("PROVIDER_BIDS_consumer_ask_id_fkey", consumerAskId, consumerAsks)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
    /** Foreign key referencing Users (database name PROVIDER_BIDS_bidder_id_fkey) */
    lazy val usersFk = foreignKey("PROVIDER_BIDS_bidder_id_fkey", bidderId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table ProviderBids */
  lazy val providerBids = new TableQuery(tag => new ProviderBids(tag))

  /** GetResult implicit for fetching ConsumerBidsRow objects using plain SQL queries */
  implicit def GetResultConsumerBidsRow(
    implicit e0: GR[Long],
    e1: GR[java.util.UUID],
    e2: GR[Option[Boolean]],
    e3: GR[OffsetDateTime],
    e4: GR[Int]
  ): GR[ConsumerBid] = GR { prs =>
    import prs._
    ConsumerBid.tupled(
      (<<[Long], <<[Long], <<[java.util.UUID], <<[Int], <<?[Boolean], <<[Boolean], <<[Boolean], <<[Boolean], <<[OffsetDateTime], <<[OffsetDateTime])
    )
  }

  /** Table description of table CONSUMER_BIDS. Objects of this class serve as prototypes for rows in queries. */
  class ConsumerBids(_tableTag: Tag) extends profile.api.Table[ConsumerBid](_tableTag, "CONSUMER_BIDS") {

    def * = (id, providerAskId, bidderId, passengers, active, confirmed, charged, refunded, createdOn, modifiedOn) <> (ConsumerBid.tupled, ConsumerBid.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(providerAskId), Rep.Some(bidderId), Rep.Some(passengers
    ), active, confirmed, charged, refunded, Rep.Some(createdOn), Rep.Some(modifiedOn)).shaped.<>(
      { r => import r._; _1.map(
        _ => ConsumerBid.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6, _7, _8, _9.get, _10.get))
      )
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column provider_ask_id SqlType(int8) */
    val providerAskId: Rep[Long] = column[Long]("provider_ask_id")
    /** Database column bidder_id SqlType(uuid) */
    val bidderId: Rep[java.util.UUID] = column[java.util.UUID]("bidder_id")
    /** Database column passengers SqlType(int8) */
    val passengers: Rep[Int] = column[Int]("passengers")
    /** Database column active SqlType(bool), Default(Some(true)) */
    val active: Rep[Option[Boolean]] = column[Option[Boolean]]("active", O.Default(Some(true)))
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val confirmed: Rep[Boolean] = column[Boolean]("confirmed", O.Default(false))
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val charged: Rep[Boolean] = column[Boolean]("charged", O.Default(false))
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val refunded: Rep[Boolean] = column[Boolean]("refunded", O.Default(false))
    /** Database column created_on SqlType(OffsetDateTime) */
    val createdOn: Rep[OffsetDateTime] = column[OffsetDateTime]("created_on")
    /** Database column modified_on SqlType(OffsetDateTime) */
    val modifiedOn: Rep[OffsetDateTime] = column[OffsetDateTime]("modified_on")

    /** Foreign key referencing ProviderAsks (database name CONSUMER_BIDS_provider_ask_id_fkey) */
    lazy val providerAsksFk = foreignKey("CONSUMER_BIDS_provider_ask_id_fkey", providerAskId, providerAsks)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
    /** Foreign key referencing Users (database name CONSUMER_BIDS_bidder_id_fkey) */
    lazy val usersFk = foreignKey("CONSUMER_BIDS_bidder_id_fkey", bidderId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table ConsumerBids */
  lazy val consumerBids = new TableQuery(tag => new ConsumerBids(tag))
}