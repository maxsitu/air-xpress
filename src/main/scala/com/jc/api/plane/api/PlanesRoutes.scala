package com.jc.api.plane.api

import java.util.UUID

import com.typesafe.scalalogging.StrictLogging
import akka.http.scaladsl.server.Directives._
import com.jc.api.common.api.RoutesSupport
import com.jc.api.plane.application.PlaneService
import com.jc.api.plane.domain.Plane
import com.jc.api.user.UserId
import com.jc.api.user.api.SessionSupport
import io.circe.generic.auto._

/**
  * Created by walle on 7/10/17.
  */
trait PlanesRoutes extends RoutesSupport with StrictLogging with SessionSupport{

  def planeService: PlaneService

  implicit val planeDataCbs = CanBeSerialized[Plane]

  val planesRoutes = pathPrefix("planes") {
    put {
      userFromSession { _ =>
        entity(as[PlaneInput]) { planeInput =>
          onSuccess(planeService.add(planeInput.asPlane)) { _ =>
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
          completeOk
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

case class PlaneInput (
  nNum:             String,
  manufacturerName: String,
  sn:               String,
  model:            String,
  ownerId:          UUID,
  pilotSeats:       Int,
  pilotMinReq:      Int,
  customerSeats:    Int
) {
  def asPlane = Plane(Some(0), nNum, manufacturerName, sn, model, ownerId, pilotSeats, pilotMinReq, customerSeats)
}

