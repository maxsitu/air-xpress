package com.jc.api.plane.application

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.plane.PlaneId
import com.jc.api.plane.domain.Plane
import com.jc.api.user.UserId

import scala.concurrent.{ExecutionContext, Future}

class PlaneDao(protected val database: SqlDatabase)(implicit val ec: ExecutionContext) extends SqlPlaneSchema {
  import database._
  import database.driver.api._

  def add(plane: Plane): Future[PlaneId] = db.run((planes returning planes.map(_.id)) += plane)

  def findById(orderId: PlaneId): Future[Option[Plane]] =
    db.run(planes.filter(_.id === orderId).result.headOption)

  def findByOwnerId(ownerId: UserId): Future[Seq[Plane]] =
    db.run(planes.filter(_.ownerId === ownerId).result)

  def findByNNumer(nNum: String): Future[Option[Plane]] =
    db.run(planes.filter(_.nNum === nNum).result.headOption)

  def count(): Future[Int] =
    db.run(planes.length.result)

  def range(offset: Int, limit: Int): Future[Seq[Plane]] =
    db.run(planes.drop(offset).take(limit).result)
}

trait SqlPlaneSchema {
  protected val database: SqlDatabase
  import database._
  import database.driver.api._

  protected val planes = TableQuery[Planes]

  protected class Planes(tag: Tag) extends Table[Plane](tag, "planes") {
    def id              = column[PlaneId]("id", O.PrimaryKey, O.AutoInc)
    def nNum            = column[String]("n_no")
    def manufacturerName= column[String]("manufacturer_name")
    def sn              = column[String]("serial_no")
    def model           = column[String]("model")
    def ownerId         = column[UserId]("owner_id")
    def pilotSeats      = column[Int]("pilot_seats")
    def minimumPilotNum = column[Int]("min_pilot")
    def customerSeats   = column[Int]("customer_seats")

    def * = (id.?, nNum, manufacturerName, sn, model, ownerId, pilotSeats, minimumPilotNum, customerSeats) <> (Plane.tupled, Plane.unapply _)
  }
}