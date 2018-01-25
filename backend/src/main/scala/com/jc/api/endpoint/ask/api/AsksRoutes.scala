package com.jc.api.endpoint.ask.api

import akka.http.scaladsl.model.StatusCodes
import com.jc.api.common.api.RoutesSupport
import com.jc.api.endpoint.ask.application.AskService
import com.jc.api.endpoint.user.api.SessionSupport
import com.typesafe.scalalogging.{Logger, StrictLogging}
import akka.http.scaladsl.server.Directives._
import com.jc.api.endpoint.flight.api.BasicFlightPlan
import com.jc.api.model.ProviderAsk
import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor}
import io.circe.generic.auto._

import scala.util.{Failure, Success}

/// TODO: add routes for asks
trait AsksRoutes extends RoutesSupport with StrictLogging with SessionSupport{
  def askService: AskService

  implicit val fullProviderAskDataCb = CanBeSerialized[FullProviderAskData]
  implicit val providerAsksCb = CanBeSerialized[Seq[ProviderAsk]]

  val providerAsksRoutes = pathPrefix("providerAsk") {
    post {
      userIdFromSession { userId =>
        entity(as[BasicProviderAskData]) { in =>
          logger.info(s"provider ask: $in")
          onComplete(askService.addBasicProviderAsk(userId, in)) {
            case Success(askId) =>
              complete("success")
          }
        }
      }
    } ~
    get {
      userIdFromSession { userId =>
        onComplete(askService.findActiveProviderAsksByProviderId(userId)) {
          case Failure(msg)  => complete(StatusCodes.InternalServerError)
          case Success(asks) => complete(asks)
        }
      }
    } ~
    get {
      parameter('orderBy.?, 'offset.as[Int], 'limit.as[Int], 'desc.as[Boolean].?) { (orderBy, offset, limit, desc) =>
        onComplete(askService.findProviderAsks(orderBy, offset, limit, desc.getOrElse(true))){
          case Success(asks) => complete(asks)
          case Failure(msg)  => complete(StatusCodes.InternalServerError)
        }
      }
    }
  }
}

case class FullProviderAskData(ask: ProviderAsk, flightPlan: BasicFlightPlan)

case class BasicProviderAskData(
  seats: Int,
  price: Double,
  plan: BasicFlightPlan
)