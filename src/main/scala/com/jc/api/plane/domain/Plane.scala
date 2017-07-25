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