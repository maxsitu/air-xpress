package com.jc.api.endpoint.ask.api

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

import scala.util.Success

/// TODO: add routes for asks
trait AsksRoutes extends RoutesSupport with StrictLogging with SessionSupport{
  def askService: AskService

  implicit val fullProviderAskDataCb = CanBeSerialized[FullProviderAskData]

  val providerAsksRoutes = pathPrefix("providerAsk") {
    put {
      userIdFromSession { userId =>
        entity(as[BasicProviderAskData]) { in =>
          logger.info(s"provider ask: $in")
          onComplete(askService.addBasicProviderAsk(userId, in)) {
            case Success(askId) =>
              complete("success")
          }
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