package com.jc.api.endpoint.location.application

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.endpoint.location.LocationId
import com.jc.api.model.Location

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by walle on 7/14/17.
  */
class LocationDao (protected val database: SqlDatabase)(implicit val ec: ExecutionContext) extends SqlLocationSchema {

  import database._
  import database.profile.api._

  def add(loc: Location): Future[LocationId] =
    db.run((locations returning locations.map(_.id)) += loc)

  def update(loc: Location): Future[Int] = {
    val q = for {l <- locations if l.id === loc.id} yield (l.code, l.name, l.geoLat, l.geoLon)
    db.run(q.update(loc.code, loc.name, loc.geoLat, loc.geoLon))
  }

  def findAll(): Future[Seq[Location]] =
    db.run(locations.result)

  def findById(locationId: LocationId): Future[Option[Location]] =
    db.run(locations.filter(_.id === locationId).result.headOption)

  def findByCode(code: String): Future[Option[Location]] =
    db.run(locations.filter(_.code === code).result.headOption)

  def findByCodePrefix(codePrefix: String): Future[Seq[Location]] =
    db.run(locations.filter(_.code like s"${codePrefix}%").result)

  def findByNamePrefix(namePrefix: String): Future[Seq[Location]] =
    db.run(locations.filter(_.name.toLowerCase like s"${namePrefix}%").result)

  def delete(id: LocationId): Future[Int] =
    db.run(locations.filter(_.id === id).delete)
}

trait SqlLocationSchema {
  protected val database: SqlDatabase

  import database.profile.api._

  protected val locations = TableQuery[Locations]

  protected class Locations(tag: Tag) extends Table[Location](tag, "LOCATIONS") {
    def id    = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def code  = column[String]("code", O.Unique)
    def name  = column[String]("name")
    def geoLat  = column[Double]("geo_lat")
    def geoLon  = column[Double]("geo_lon")

    def * = (id, code, name, geoLat, geoLon) <> (Location.tupled, Location.unapply _)
  }
}