package com.jc.api.endpoint.plane.application

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.endpoint.plane.PlaneId
import com.jc.api.endpoint.user.UserId
import com.jc.api.model.Plane
import com.jc.api.schema.SqlAccountServiceSchema

import scala.concurrent.{ExecutionContext, Future}

class PlaneDao(protected val database: SqlDatabase)(implicit val ec: ExecutionContext) extends SqlAccountServiceSchema {
  import database._
  import database.profile.api._

  private def findOneWhere(condition: Planes => Rep[Boolean]) = db.run(planes.filter(condition).result.headOption)
  private def findWhere(condition: Planes => Rep[Boolean]) = db.run(planes.filter(condition).result)

  def add(plane: Plane): Future[PlaneId] = db.run((planes returning planes.map(_.id)) += plane)
  def findById(orderId: PlaneId): Future[Option[Plane]] = findOneWhere(_.id === orderId)
  def findByOwnerId(ownerId: UserId): Future[Seq[Plane]] = findWhere(_.ownerId === ownerId)
  def findByNNumber(nNum: String): Future[Option[Plane]] = findOneWhere(_.nNo === nNum)
  def count(): Future[Int] = db.run(planes.length.result)
  def range(offset: Int, limit: Int): Future[Seq[Plane]] = db.run(planes.drop(offset).take(limit).result)
}