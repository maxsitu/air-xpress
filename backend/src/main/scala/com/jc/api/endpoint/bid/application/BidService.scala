package com.jc.api.endpoint.bid.application

import com.jc.api.endpoint.ask.AskId
import com.jc.api.endpoint.ask.application.AskDao
import com.jc.api.endpoint.bid.BidId
import com.jc.api.endpoint.user.UserId
import com.jc.api.model.ConsumerBid

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class BidService (
  bidDao: BidDao,
  askDao: AskDao
)(implicit ec: ExecutionContext){
  def confirmProiderBidByProiderBidId(providerBidId: BidId): Future[Unit] = {
    bidDao.confirmProviderBid(providerBidId)
  }

  def confirmConsumerBidByConsumerBidId(consumerBidId: BidId): Future[Either[String, Int]] = {
    bidDao.confirmConsumerBidTransactionally(consumerBidId) map (Right(_)) recover {
      case e: Throwable => Left(e.getMessage)
    }
  }

  def getProviderIdByConsumerBidId(consumerBidId: BidId): Future[UserId] = {
    bidDao.findConsumerBidById(consumerBidId) flatMap {consumerBid =>
      askDao.findProviderAskByConsuerBid(consumerBid) map {providerAsk =>
        providerAsk.providerId
      }
    }
  }

  def getConsumerBidsByProviderAskId(askId: AskId, isConfirmed: Option[Boolean]): Future[Seq[ConsumerBid]] = {
    isConfirmed match {
      case Some(true) =>
        bidDao.findConfirmedConsumerBidsByProviderAskId(askId)
      case Some(false) =>
        bidDao.findUnconfirmedConsumerBidsByProviderAskId(askId)
      case None =>
        bidDao.findConsumerBidsByProviderAskId(askId)
    }
  }

  def chargeStripe(token: String): Future[Try[Unit]] = {
    ???
    // TODO: finish logic charging via stripe
  }
}
