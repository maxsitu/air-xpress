package com.jc.api.endpoint.location.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.jc.api.common.api.RoutesSupport
import com.jc.api.endpoint.location.LocationId
import com.jc.api.endpoint.location.application.LocationAddResult.InvalidData
import com.jc.api.endpoint.location.application.{LocationAddResult, LocationService}
import com.jc.api.endpoint.user.api.SessionSupport
import com.jc.api.model.Location
import com.typesafe.scalalogging.StrictLogging
import io.circe.generic.auto._

trait LocationRoutes extends RoutesSupport with StrictLogging with SessionSupport {
  def locationService: LocationService

  implicit val locationDataCbs = CanBeSerialized[Location]
  implicit val locationInvalidDataCbs = CanBeSerialized[InvalidData]

  val locationRoutes = pathPrefix("location") {
    post {
      entity(as[LocationInput]) { loc =>
        onSuccess(locationService.addLocation(loc.code, loc.name, loc.geoLat, loc.geoLon)) {
          case Right(_) => completeOk
          case Left(result) => result match {
            case invalid: LocationAddResult.InvalidData => complete(StatusCodes.BadRequest, invalid)
            case LocationAddResult.LocationExists(code) => complete(StatusCodes.Conflict, s"$code exists already!")
            case LocationAddResult.Success  => complete(StatusCodes.InternalServerError, "This part of code shouldn't reach")
          }
        }
      }
    } ~
    put {
      path("id" / LongNumber) { locationId =>
        entity(as[LocationInput]) { loc =>


        }
      }
    } ~
    get {
      pathEnd {
        onSuccess(locationService.findAll()) { locations =>
          complete(locations)
        }
      } ~
      path("id" / LongNumber) { locationId =>
        onSuccess(locationService.findByLocationId(locationId)) {
          case Some(location) => complete(location)
          case _  => complete(StatusCodes.NotFound)
        }
      } ~
      path("code" / Segment) { locationCode =>
        onSuccess(locationService.findByLocationCode(locationCode)) {
          case Some(location) => complete(location)
          case _  => complete(StatusCodes.NotFound)
        }
      }
    }
  }
}

case class LocationInput (code: String, name: String, geoLat: String, geoLon: String)
