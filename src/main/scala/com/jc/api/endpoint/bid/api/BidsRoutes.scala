package com.jc.api.endpoint.bid.api

import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive0, ValidationRejection}
import com.jc.api.common.api.RoutesSupport
import com.jc.api.endpoint.user.api.SessionSupport
import com.typesafe.scalalogging.StrictLogging
import akka.http.scaladsl.server.Directives._
import com.jc.api.endpoint.bid.BidId
import com.jc.api.endpoint.bid.application.BidService
import com.jc.api.endpoint.user.application.Session
import com.jc.api.model.ConsumerBid
import com.softwaremill.session.{RefreshTokenStorage, SessionManager}
import scala.concurrent.ExecutionContext
import io.circe.generic.auto._

trait BidsRoutes extends RoutesSupport with StrictLogging with ProviderSupport{

  def bidService: BidService

  val bidsRoutes = pathPrefix("consumerBids") {
    path("confirm") {
      post {
        parameter('bidId) { bidIdStr =>
          val bidId: BidId = bidIdStr.toLong
          isProviderCurrentUser(bidId) {
            onSuccess(bidService.confirmConsumerBidByConsumerBidId(bidId)) {
              case x if x > 0 =>
                completeOk
              case _ =>
                reject(ValidationRejection("Inadequate seats"))
            }
          }
        }
      }
    } ~ pathEnd {
      put {
        entity(as[ConsumerBid]) { bid =>
          completeOk
        }
      }
    }
  }
}

trait ProviderSupport extends SessionSupport{
  implicit def sessionManager: SessionManager[Session]

  implicit def refreshTokenStorage: RefreshTokenStorage[Session]

  implicit def ec: ExecutionContext

  def bidService: BidService

  def isProviderCurrentUser(bidId: BidId): Directive0 = userIdFromSession flatMap   { userId =>
    onSuccess(bidService.getProviderIdByConsumerBidId(bidId)) flatMap {
      case `userId` => pass
      case _ => reject(AuthorizationFailedRejection)
    }
  }
}