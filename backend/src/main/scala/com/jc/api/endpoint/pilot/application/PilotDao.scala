package com.jc.api.endpoint.pilot.application

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.endpoint.pilot.PilotId
import com.jc.api.endpoint.user.UserId
import com.jc.api.endpoint.user.application.SqlUserSchema
import com.jc.api.model.Pilot
import com.jc.api.schema.SqlAccountServiceSchema

import scala.concurrent.{ExecutionContext, Future}

class PilotDao(protected val database: SqlDatabase) (implicit val ec: ExecutionContext) extends SqlPilotSchema {

  import database._
  import database.profile.api._

  private def findOneWhere(condition: Pilots => Rep[Boolean]) = db.run(pilots.filter(condition).result.headOption)

  def addPilot(pilot: Pilot): Future[Unit] = db.run(pilots += pilot).map(_ => ())
  def findPilotById(pilotId: PilotId): Future[Option[Pilot]] = findOneWhere(_.id === pilotId)
  def findPilotByUserId(userId: UserId): Future[Option[Pilot]] = findOneWhere(_.userId === userId)
  def findPilotByLicenseNum(licenseNum: String): Future[Option[Pilot]] = findOneWhere(_.licenseNum === licenseNum)
}

trait SqlPilotSchema extends SqlUserSchema {
  protected val database: SqlDatabase

  import database._
  import database.profile.api._
  import slick.jdbc.{GetResult => GR}
  import slick.model.ForeignKeyAction

  /** GetResult implicit for fetching PilotsRow objects using plain SQL queries */
  implicit def GetResultPilotsRow(
    implicit e0: GR[Int],
    e1: GR[java.util.UUID],
    e2: GR[String]): GR[Pilot] = GR { prs =>
    import prs._
    Pilot.tupled((<<[Int], <<[java.util.UUID], <<[String]))
  }

  /** Table description of table PILOTS. Objects of this class serve as prototypes for rows in queries. */
  class Pilots(_tableTag: Tag) extends profile.api.Table[Pilot](_tableTag, "PILOTS") {
    def * = (id, userId, licenseNum) <> (Pilot.tupled, Pilot.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(licenseNum)).shaped.<>(
      { r => import r._; _1.map(_ => Pilot.tupled((_1.get, _2.get, _3.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(uuid) */
    val userId: Rep[java.util.UUID] = column[java.util.UUID]("user_id")
    /** Database column license_num SqlType(varchar) */
    val licenseNum: Rep[String] = column[String]("license_num")

    /** Foreign key referencing Users (database name PILOTS_user_id_fkey) */
    lazy val usersFk = foreignKey("PILOTS_user_id_fkey", userId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table Pilots */
  lazy val pilots = new TableQuery(tag => new Pilots(tag))
}