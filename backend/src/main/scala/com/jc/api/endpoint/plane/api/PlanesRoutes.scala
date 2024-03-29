package com.jc.api.endpoint.plane.api

import akka.http.scaladsl.model.StatusCodes
import com.typesafe.scalalogging.StrictLogging
import akka.http.scaladsl.server.Directives._
import com.jc.api.common.api.RoutesSupport
import com.jc.api.endpoint.plane.application.PlaneService
import com.jc.api.endpoint.user.api.SessionSupport
import com.jc.api.model.{BasicPlaneData, Plane}
import io.circe.generic.auto._

/**
  * Created by walle on 7/10/17.
  */
trait PlanesRoutes extends RoutesSupport with StrictLogging with SessionSupport{

  def planeService: PlaneService

  implicit val planeDataCbs = CanBeSerialized[Plane]
  implicit val basicPlaneDataCbs = CanBeSerialized[BasicPlaneData]

  val planesRoutes = pathPrefix("planes") {
    post {
      userIdFromSession{ userId =>
        entity(as[BasicPlaneData]) { planeInput =>
          onSuccess(planeService.add(planeInput.asPlane(userId))) { _ =>
            completeOk
          }
        }
      }
    } ~
    put {
      path("id" / IntNumber) { planeId =>
        entity(as[BasicPlaneData]) { p =>
          onSuccess(planeService.update(planeId, p.nNo, p.manufacturerName, p.serialNo, p.model, p.pilotSeats, p.minPilot, p.customerSeats)) {
            case _: Int => completeOk
            case _ => complete(StatusCodes.InternalServerError)
          }
        }

      }
    } ~
    get {
      pathEnd {
        parameters('offset.?, 'limit.?) { (offset, limit) =>
          val offsetVal = offset.map(_.toInt).getOrElse(0)
          val limitVal: Int  = limit.map(_.toInt).getOrElse(500)
          onSuccess(planeService.range(offsetVal, limitVal)) { planes =>
            complete(
              planes.map {
                BasicPlaneData.fromPlane(_)
              }
            )
          }
        }
      } ~
      path("ownerId" / JavaUUID) { ownerId =>
        userFromSession { user =>
          onSuccess(planeService.findByOwnerId(ownerId)) { planes =>
            complete(
              planes.map {
                BasicPlaneData.fromPlane(_)
              }
            )
          }
        }
      } ~
      path("planeId" / IntNumber) { planeId =>
        userFromSession { user =>
          onSuccess(planeService.findById(planeId)) { planes =>
            complete(planes)
          }
        }
      }
    }
  }
}



