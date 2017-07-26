package com.jc.api.plane.api

import java.util.UUID

import com.typesafe.scalalogging.StrictLogging
import akka.http.scaladsl.server.Directives._
import com.jc.api.common.api.RoutesSupport
import com.jc.api.plane.application.PlaneService
import com.jc.api.plane.domain.{BasicPlaneData, Plane}
import com.jc.api.user.UserId
import com.jc.api.user.api.SessionSupport
import io.circe.generic.auto._

/**
  * Created by walle on 7/10/17.
  */
trait PlanesRoutes extends RoutesSupport with StrictLogging with SessionSupport{

  def planeService: PlaneService

  implicit val planeDataCbs = CanBeSerialized[Plane]
  implicit val basicPlaneDataCbs = CanBeSerialized[BasicPlaneData]

  val planesRoutes = pathPrefix("planes") {
    put {
      userIdFromSession{ userId =>
        entity(as[BasicPlaneData]) { planeInput =>
          onSuccess(planeService.add(planeInput.asPlane(userId))) { _ =>
            completeOk
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
          onSuccess(planeService.findByOwnerId(ownerId)) { orders =>
            complete(orders)
          }
        }
      } ~
      path("planeId" / LongNumber) { planeId =>
        userFromSession { user =>
          onSuccess(planeService.findById(planeId)) { orders =>
            complete(orders)
          }
        }
      }
    }
  }
}



