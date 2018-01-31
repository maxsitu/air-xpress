package com.jc.api.endpoint.plane.application

import com.jc.api.endpoint.plane.PlaneId
import com.jc.api.endpoint.user.UserId
import com.jc.api.model.Plane

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by walle on 7/10/17.
  */
class PlaneService(
  planeDao: PlaneDao
)(implicit ec: ExecutionContext){

  def add(plane: Plane): Future[PlaneId] = planeDao.add(plane)
  def update(
    id: PlaneId, nNo: String, manufacturerName: String, serialNo: String,
    model: String, pilotSeats: Int, minPilot: Int, customerSeats: Int
  ): Future[Int] = {
    planeDao.update(id, nNo, manufacturerName, serialNo, model, pilotSeats, minPilot, customerSeats)
  }

  def findById(planeId: PlaneId): Future[Option[Plane]] = planeDao.findById(planeId)
  def findByOwnerId(ownerId: UserId): Future[Seq[Plane]] = planeDao.findByOwnerId(ownerId)
  def count(): Future[Int] = planeDao.count()
  def range(offset: Int, limit: Int) = planeDao.range(offset, limit)

}
