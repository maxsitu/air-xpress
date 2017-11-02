package com.jc.api.endpoint.ask.application

import com.jc.api.common.Utils
import com.jc.api.common.sql.SqlDatabase
import com.jc.api.endpoint.ask.AskId
import com.jc.api.endpoint.user.UserId
import com.jc.api.model.{ConsumerAsk, ConsumerBid, ProviderAsk, ProviderBid}
import com.jc.api.schema.SqlAccountServiceSchema

import scala.concurrent.{ExecutionContext, Future}

class AskDao (protected val database: SqlDatabase)(implicit val ec: ExecutionContext) extends SqlAccountServiceSchema {

  import database._
  import database.profile.api._

  private def findOneProviderAskWhere(condition: ProviderAsks => Rep[Boolean]) = db.run(providerAsks.filter(condition).result.headOption)
  private def findProviderAskWhere(condition: ProviderAsks => Rep[Boolean]) = db.run(providerAsks.filter(condition).result)
  private def findOneConsumerAskWhere(condition: ConsumerAsks => Rep[Boolean]) = db.run(consumerAsks.filter(condition).result.headOption)
  private def findConsumerAskWhere(condition: ConsumerAsks => Rep[Boolean]) = db.run(consumerAsks.filter(condition).result)

  def addProviderAsk(providerAsk: ProviderAsk): Future[AskId] = db.run((providerAsks returning providerAsks.map(_.id)) += providerAsk)
  def addConsumerAsk(consumerAsk: ConsumerAsk): Future[AskId] = db.run((consumerAsks returning consumerAsks.map(_.id)) += consumerAsk)

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
