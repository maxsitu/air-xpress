package com.jc.api.plane.domain

import com.jc.api.plane.PlaneId
import com.jc.api.user.UserId

case class Plane (
  id: Option[PlaneId],
  nNum: String,
  manufacturerName: String,
  sn: String,
  model: String,
  owner: UserId,
  pilotSeats: Int,
  minimumPilotNum: Int,
  customerSeats: Int
)

case class BasicPlaneData(
                           nNum:             String,
                           manufacturerName: String,
                           sn:               String,
                           model:            String,
                           pilotSeats:       Int,
                           pilotMinReq:      Int,
                           customerSeats:    Int
                         ) {
  def asPlane(ownerId: UserId) = Plane(Some(0), nNum, manufacturerName, sn, model, ownerId, pilotSeats, pilotMinReq, customerSeats)
}

object BasicPlaneData {
  def fromPlane(plane: Plane) =
    new BasicPlaneData(
      plane.nNum,
      plane.manufacturerName,
      plane.sn,
      plane.model,
      plane.pilotSeats,
      plane.minimumPilotNum,
      plane.customerSeats)
}
