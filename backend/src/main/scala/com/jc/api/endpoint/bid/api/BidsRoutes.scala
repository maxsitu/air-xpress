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

import scala.concurrent.{ExecutionContext, Future}
import io.circe.generic.auto._

import scala.util.{Failure, Success}

trait BidsRoutes extends RoutesSupport with StrictLogging with ProviderSupport{

  def bidService: BidService

  implicit val consumerBidCbs  = CanBeSerialized[ConsumerBid]

  val bidsRoutes = pathPrefix("consumerBids") {
    post {
      path("charge" / Segment) { token =>
        onComplete(bidService.chargeStripe(token)) {
          case Success(value) => completeOk
          case _ => completeOk
        }
      }
    } ~
    path("confirm") {
      post {
        parameter('bidId.as[BidId]) { bidId =>
          isProviderCurrentUser(bidId) {
            onSuccess(bidService.confirmConsumerBidByConsumerBidId(bidId)) {
              case Right(x) if x > 0 =>
                completeOk
              case _ =>
                reject(ValidationRejection("Inadequate seats"))
            }
          }
        }
      }
    } ~
    pathEnd {
      put {
        entity(as[ConsumerBid]) { bid =>
          /// TODO: insert consumer bid record
          completeOk
        }
      } ~
      get {
        parameters(('providerAskId.as[Long], 'isConfirmed.?)) { (askId, isConfirmedOpt) =>
          val isConfirmed = isConfirmedOpt.map(_.toBoolean)
          onSuccess(bidService.getConsumerBidsByProviderAskId(askId, isConfirmed)) { bids =>
            complete(bids)
          }
        }
      }
    }
  }
  /// TODO: add routes for provider bids
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