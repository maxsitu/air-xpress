package com.jc.api.endpoint.ask.application

import com.jc.api.endpoint.ask.AskId
import com.jc.api.endpoint.bid.BidId
import com.jc.api.endpoint.bid.application.BidDao
import com.jc.api.endpoint.user.UserId
import com.jc.api.model.{ConsumerAsk, ConsumerBid, ProviderAsk, ProviderBid}

import scala.concurrent.{ExecutionContext, Future}
class AskService (
  askDao: AskDao
)(implicit ec: ExecutionContext){

  def addProviderAsk(ask: ProviderAsk): Future[AskId] = askDao.addProviderAsk(ask)
  def addConsumerAsk(ask: ConsumerAsk): Future[AskId] = askDao.addConsumerAsk(ask)

  def findProviderAsksByProviderId(providerId: UserId): Future[Seq[ProviderAsk]] = askDao.findProviderAsksByProviderId(providerId)
  def findConsumerAsksByConsumerId(consumerId: UserId): Future[Seq[ConsumerAsk]] = askDao.findConsumerAsksByConsumerId(consumerId)

  def findActiveProviderAsksByProviderId(providerId: UserId): Future[Seq[ProviderAsk]] = askDao.findActiveProviderAsksByProviderId(providerId)
  def findActiveConsumerAsksByProviderId(consumerId: UserId): Future[Seq[ConsumerAsk]] = askDao.findActiveConsumerAsksByConsumerId(consumerId)

  def findProviderAskByConsumerBid(consumerBid: ConsumerBid): Future[ProviderAsk] = askDao.findProviderAskByConsuerBid(consumerBid)
  def findConsumerAskByProviderBid(providerBid: ProviderBid): Future[ConsumerAsk] = askDao.findConsumerAskByProviderBid(providerBid)
}
