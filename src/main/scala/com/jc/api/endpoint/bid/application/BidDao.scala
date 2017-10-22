package com.jc.api.endpoint.bid.application

import com.jc.api.common.Utils
import com.jc.api.common.sql.SqlDatabase
import com.jc.api.endpoint.ask.AskId
import com.jc.api.endpoint.bid.BidId
import com.jc.api.endpoint.user.UserId
import com.jc.api.model.{ConsumerBid, ProviderAsk, ProviderBid}
import com.jc.api.schema.SqlAccountServiceSchema
import slick.jdbc
import slick.jdbc.TransactionIsolation

import scala.concurrent.{ExecutionContext, Future}

class BidDao (protected val database: SqlDatabase)(implicit val ec: ExecutionContext) extends SqlAccountServiceSchema {

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

  def setProviderBidStatus(bidId: BidId, isActive: Boolean): Future[Unit] =
    db.run(providerAsks.filter(_.id === bidId).map(b => (b.active, b.modifiedOn)).update((Some(isActive), Utils.now()))).map(_ => ())
  def setConsumerBidStatus(bidId: BidId, isActive: Boolean): Future[Unit] =
    db.run(consumerAsks.filter(_.id === bidId).map(b => (b.active, b.modifiedOn)).update((Some(isActive), Utils.now()))).map(_ => ())

  def confirmProviderBid(bidId: BidId): Future[Unit] =
    db.run(providerBids.filter(_.id === bidId).map(b => (b.confirmed, b.modifiedOn)).update((Some(true), Utils.now())).map(_ => ()))
  def confirmConsumerBid(bidId: BidId): Future[Unit] =
    db.run(consumerBids.filter(_.id === bidId).map(b => (b.confirmed, b.modifiedOn)).update((Some(true), Utils.now())).map(_ => ()))

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
            consumerBids.filter(_.id === bidId).map(_.confirmed).update(Some(true))
          }

        case _ => DBIO.failed(new Exception("Invalid result"))
      }.transactionally

    db.run(a)
  }

  def countConfirmedPassengersByProviderAskId(askId: AskId): Future[Int] = {
    val query = consumerBids.filter(_.confirmed === true).filter(_.providerAskId === askId).map(_.passengers).sum

    db.run(query.result).map(cntOpt => cntOpt.getOrElse(0))
  }
}
