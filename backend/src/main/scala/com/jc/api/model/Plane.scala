package com.jc.api.model

import com.jc.api.endpoint.plane.PlaneId
import com.jc.api.endpoint.user.UserId

/** Entity class storing rows of table Planes
  *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
  *  @param nNo Database column n_no SqlType(varchar)
  *  @param manufacturerName Database column manufacturer_name SqlType(varchar)
  *  @param serialNo Database column serial_no SqlType(varchar)
  *  @param model Database column model SqlType(varchar)
  *  @param ownerId Database column owner_id SqlType(uuid)
  *  @param pilotSeats Database column pilot_seats SqlType(int4)
  *  @param minPilot Database column min_pilot SqlType(int4)
  *  @param customerSeats Database column customer_seats SqlType(int4) */
case class Plane(
  id              : PlaneId,
  nNo             : String,
  manufacturerName: String,
  serialNo        : String,
  model           : String,
  ownerId         : UserId,
  pilotSeats      : Int,
  minPilot        : Int,
  customerSeats   : Int
)

case class BasicPlaneData(
  nNo             : String,
  manufacturerName: String,
  serialNo        : String,
  model           : String,
  pilotSeats      : Int,
  minPilot     : Int,
  customerSeats   : Int
) {
  def asPlane(ownerId: UserId) = Plane(
    0, nNo, manufacturerName, serialNo, model, ownerId, pilotSeats, minPilot, customerSeats
  )
}

object BasicPlaneData {
  def fromPlane(plane: Plane) =
    new BasicPlaneData(
      plane.nNo,
      plane.manufacturerName,
      plane.serialNo,
      plane.model,
      plane.pilotSeats,
      plane.minPilot,
      plane.customerSeats
    )
}
