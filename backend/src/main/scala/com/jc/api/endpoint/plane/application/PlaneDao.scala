package com.jc.api.endpoint.plane.application

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.endpoint.plane.PlaneId
import com.jc.api.endpoint.user.UserId
import com.jc.api.endpoint.user.application.SqlUserSchema
import com.jc.api.model.Plane

import scala.concurrent.{ExecutionContext, Future}

class PlaneDao(protected val database: SqlDatabase)(implicit val ec: ExecutionContext) extends SqlPlaneSchema {
  import database._
  import database.profile.api._

  private def findOneWhere(condition: Planes => Rep[Boolean]) = db.run(planes.filter(condition).result.headOption)
  private def findWhere(condition: Planes => Rep[Boolean]) = db.run(planes.filter(condition).result)

  def add(plane: Plane): Future[PlaneId] = db.run((planes returning planes.map(_.id)) += plane)
  def update(id: PlaneId, nNo: String, manufacturerName: String, serialNo: String, model: String, pilotSeats: Int, minPilot: Int, customerSeats: Int): Future[Int] = db.run(
    planes.filter(_.id=== id)
      .map(p =>
        (p.nNo, p.manufacturerName, p.serialNo, p.model, p.pilotSeats, p.minPilot, p.customerSeats)
      ).update(nNo, manufacturerName, serialNo, model, pilotSeats, minPilot, customerSeats)
  )
  def findById(orderId: PlaneId): Future[Option[Plane]] = findOneWhere(_.id === orderId)
  def findByOwnerId(ownerId: UserId): Future[Seq[Plane]] = findWhere(_.ownerId === ownerId)
  def findByNNumber(nNum: String): Future[Option[Plane]] = findOneWhere(_.nNo === nNum)
  def count(): Future[Int] = db.run(planes.length.result)
  def range(offset: Int, limit: Int): Future[Seq[Plane]] = db.run(planes.drop(offset).take(limit).result)
}

trait SqlPlaneSchema extends SqlUserSchema {
  protected val database: SqlDatabase

  import database._
  import database.profile.api._
  import slick.jdbc.{GetResult => GR}
  import slick.model.ForeignKeyAction

  /** Entity class storing rows of table PlaneProviderMappings
    *
    * @param providerId Database column provider_id SqlType(int4)
    * @param planeId    Database column plane_id SqlType(int4) */
  case class PlaneProviderMappingsRow(providerId: Int, planeId: Int)

  /** GetResult implicit for fetching PlaneProviderMappingsRow objects using plain SQL queries */
  implicit def GetResultPlaneProviderMappingsRow(implicit e0: GR[Int]): GR[PlaneProviderMappingsRow] = GR {
    prs =>
      import prs._
      PlaneProviderMappingsRow.tupled((<<[Int], <<[Int]))
  }

  /** Table description of table PLANE_PROVIDER_MAPPINGS. Objects of this class serve as prototypes for rows in queries. */
  class PlaneProviderMappings(_tableTag: Tag) extends profile.api.Table[PlaneProviderMappingsRow](
    _tableTag, "PLANE_PROVIDER_MAPPINGS"
  ) {
    def * = (providerId, planeId) <> (PlaneProviderMappingsRow.tupled, PlaneProviderMappingsRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(providerId), Rep.Some(planeId)).shaped.<>(
      { r => import r._; _1.map(_ => PlaneProviderMappingsRow.tupled((_1.get, _2.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column provider_id SqlType(int4) */
    val providerId: Rep[Int] = column[Int]("provider_id")
    /** Database column plane_id SqlType(int4) */
    val planeId: Rep[Int] = column[Int]("plane_id")

    /** Foreign key referencing Planes (database name PLANE_PROVIDER_MAPPINGS_plane_id_fkey) */
    lazy val planesFk = foreignKey("PLANE_PROVIDER_MAPPINGS_plane_id_fkey", planeId, planes)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
    /** Foreign key referencing PlaneProviders (database name PLANE_PROVIDER_MAPPINGS_provider_id_fkey) */
    lazy val planeProvidersFk = foreignKey("PLANE_PROVIDER_MAPPINGS_provider_id_fkey", providerId, planeProviders)(
      r => r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade
    )
  }

  /** Collection-like TableQuery object for table PlaneProviderMappings */
  lazy val planeProviderMappings = new TableQuery(tag => new PlaneProviderMappings(tag))

  /** Entity class storing rows of table PlaneProviders
    *
    * @param id          Database column id SqlType(serial), AutoInc, PrimaryKey
    * @param userId      Database column user_id SqlType(uuid)
    * @param lincenceNum Database column lincence_num SqlType(varchar) */
  case class PlaneProvidersRow(
    id: Int,
    userId: java.util.UUID,
    lincenceNum: String
  )

  /** GetResult implicit for fetching PlaneProvidersRow objects using plain SQL queries */
  implicit def GetResultPlaneProvidersRow(
    implicit e0: GR[Int],
    e1: GR[java.util.UUID],
    e2: GR[String]
  ): GR[PlaneProvidersRow] = GR { prs =>
    import prs._
    PlaneProvidersRow.tupled((<<[Int], <<[java.util.UUID], <<[String]))
  }

  /** Table description of table PLANE_PROVIDERS. Objects of this class serve as prototypes for rows in queries. */
  class PlaneProviders(_tableTag: Tag) extends profile.api.Table[PlaneProvidersRow](_tableTag, "PLANE_PROVIDERS") {
    def * = (id, userId, lincenceNum) <> (PlaneProvidersRow.tupled, PlaneProvidersRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(lincenceNum)).shaped.<>(
      { r => import r._; _1.map(_ => PlaneProvidersRow.tupled((_1.get, _2.get, _3.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(uuid) */
    val userId: Rep[java.util.UUID] = column[java.util.UUID]("user_id")
    /** Database column lincence_num SqlType(varchar) */
    val lincenceNum: Rep[String] = column[String]("lincence_num")

    /** Foreign key referencing Users (database name PLANE_PROVIDERS_user_id_fkey) */
    lazy val usersFk = foreignKey("PLANE_PROVIDERS_user_id_fkey", userId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table PlaneProviders */
  lazy val planeProviders = new TableQuery(tag => new PlaneProviders(tag))

  /** GetResult implicit for fetching PlanesRow objects using plain SQL queries */
  implicit def GetResultPlanesRow(
    implicit e0: GR[Int],
    e1: GR[String],
    e2: GR[java.util.UUID]
  ): GR[Plane] = GR { prs =>
    import prs._
    Plane.tupled(
      (<<[Int], <<[String], <<[String], <<[String], <<[String], <<[java.util.UUID], <<[Int], <<[Int], <<[Int])
    )
  }

  /** Table description of table PLANES. Objects of this class serve as prototypes for rows in queries. */
  class Planes(_tableTag: Tag) extends profile.api.Table[Plane](_tableTag, "PLANES") {
    def * = (id, nNo, manufacturerName, serialNo, model, ownerId, pilotSeats, minPilot, customerSeats) <> (Plane.tupled, Plane.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(nNo), Rep.Some(manufacturerName), Rep.Some(serialNo), Rep.Some(model), Rep.Some(
      ownerId
    ), Rep.Some(pilotSeats), Rep.Some(minPilot), Rep.Some(customerSeats)).shaped.<>(
      { r => import r._; _1.map(
        _ => Plane.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get))
      )
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column n_no SqlType(varchar) */
    val nNo: Rep[String] = column[String]("n_no")
    /** Database column manufacturer_name SqlType(varchar) */
    val manufacturerName: Rep[String] = column[String]("manufacturer_name")
    /** Database column serial_no SqlType(varchar) */
    val serialNo: Rep[String] = column[String]("serial_no")
    /** Database column model SqlType(varchar) */
    val model: Rep[String] = column[String]("model")
    /** Database column owner_id SqlType(uuid) */
    val ownerId: Rep[java.util.UUID] = column[java.util.UUID]("owner_id")
    /** Database column pilot_seats SqlType(int4) */
    val pilotSeats: Rep[Int] = column[Int]("pilot_seats")
    /** Database column min_pilot SqlType(int4) */
    val minPilot: Rep[Int] = column[Int]("min_pilot")
    /** Database column customer_seats SqlType(int4) */
    val customerSeats: Rep[Int] = column[Int]("customer_seats")

    /** Foreign key referencing Users (database name PLANES_owner_id_fkey) */
    lazy val usersFk = foreignKey("PLANES_owner_id_fkey", ownerId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table Planes */
  lazy val planes = new TableQuery(tag => new Planes(tag))
}