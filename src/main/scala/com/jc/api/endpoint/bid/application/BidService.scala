package com.jc.api.endpoint.bid.application

import com.jc.api.endpoint.ask.application.AskDao
import com.jc.api.endpoint.bid.BidId
import com.jc.api.endpoint.user.UserId

import scala.concurrent.{ExecutionContext, Future}

class BidService (
  bidDao: BidDao,
  askDao: AskDao
)(implicit ec: ExecutionContext){
  def confirmProiderBidByProiderBidId(providerBidId: BidId): Future[Unit] = {
    bidDao.confirmProviderBid(providerBidId)
  }

  def confirmConsumerBidByConsumerBidId(consumerBidId: BidId): Future[Int] = {
    bidDao.confirmConsumerBidTransactionally(consumerBidId)
  }

  def getProviderIdByConsumerBidId(consumerBidId: BidId): Future[UserId] = {
    bidDao.findConsumerBidById(consumerBidId) flatMap {consumerBid =>
      askDao.findProviderAskByConsuerBid(consumerBid) map {providerAsk =>
        providerAsk.providerId
      }
    }
  }
}
