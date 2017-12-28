package com.jc.api.endpoint.ask.application

import java.time.{Instant, ZoneOffset}
import java.util.TimeZone

import com.jc.api.endpoint.ask.AskId
import com.jc.api.endpoint.ask.api.BasicProviderAskData
import com.jc.api.endpoint.flight.application.FlightDao
import com.jc.api.endpoint.user.UserId
import com.jc.api.model.{ConsumerAsk, ConsumerBid, ProviderAsk, ProviderBid}

import scala.concurrent.{ExecutionContext, Future}
class AskService (
  askDao: AskDao,
  flightDao: FlightDao
)(implicit ec: ExecutionContext){

  /* Section: Provider Ask */

  def addBasicProviderAsk(providerId: UserId, ask: BasicProviderAskData): Future[AskId] =
    askDao.addProviderAsk(providerId, ask)

  /**
    * Add provider ask into database
    * @param ask
    * @return
    */
  def addProviderAsk(ask: ProviderAsk): Future[AskId] = askDao.addProviderAsk(ask)

  def findProviderAsksByProviderId(providerId: UserId): Future[Seq[ProviderAsk]] =
  askDao.findProviderAsksByProviderId(providerId)

  def findActiveProviderAsksByProviderId(providerId: UserId): Future[Seq[ProviderAsk]] =
    askDao.findActiveProviderAsksByProviderId(providerId)

  def findProviderAskByConsumerBid(consumerBid: ConsumerBid): Future[ProviderAsk] =
    askDao.findProviderAskByConsuerBid(consumerBid)


  /* Section: Consumer Ask */

  /**
    * Add consumer ask into database
    * @param ask
    * @return
    */
  def addConsumerAsk(ask: ConsumerAsk): Future[AskId] = askDao.addConsumerAsk(ask)

  def findConsumerAsksByConsumerId(consumerId: UserId): Future[Seq[ConsumerAsk]] =
    askDao.findConsumerAsksByConsumerId(consumerId)

  def findActiveConsumerAsksByProviderId(consumerId: UserId): Future[Seq[ConsumerAsk]] =
    askDao.findActiveConsumerAsksByConsumerId(consumerId)


  def findConsumerAskByProviderBid(providerBid: ProviderBid): Future[ConsumerAsk] =
    askDao.findConsumerAskByProviderBid(providerBid)
}
