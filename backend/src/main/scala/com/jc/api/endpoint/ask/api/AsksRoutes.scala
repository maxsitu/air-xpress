package com.jc.api.endpoint.ask.api

import com.jc.api.common.api.RoutesSupport
import com.jc.api.endpoint.ask.application.AskService
import com.jc.api.endpoint.user.api.SessionSupport
import com.typesafe.scalalogging.StrictLogging
import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._

/// TODO: add routes for asks
trait AsksRoutes extends RoutesSupport with StrictLogging with SessionSupport{
  def askService: AskService

//  val providerAsksRoutes = pathPrefix("providerAsk") {
//    put {
//      userIdFromSession { userId =>
//        entity(as[])
//      }
//    }
//  }
}

//case