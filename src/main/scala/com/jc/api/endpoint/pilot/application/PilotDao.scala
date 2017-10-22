package com.jc.api.endpoint.pilot.application

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.endpoint.pilot.PilotId
import com.jc.api.endpoint.user.UserId
import com.jc.api.model.Pilot
import com.jc.api.schema.SqlAccountServiceSchema

import scala.concurrent.{ExecutionContext, Future}

class PilotDao(protected val database: SqlDatabase) (implicit val ec: ExecutionContext) extends SqlAccountServiceSchema {

  import database._
  import database.profile.api._

  private def findOneWhere(condition: Pilots => Rep[Boolean]) = db.run(pilots.filter(condition).result.headOption)

  def addPilot(pilot: Pilot): Future[Unit] = db.run(pilots += pilot).map(_ => ())
  def findPilotById(pilotId: PilotId): Future[Option[Pilot]] = findOneWhere(_.id === pilotId)
  def findPilotByUserId(userId: UserId): Future[Option[Pilot]] = findOneWhere(_.userId === userId)
  def findPilotByLicenseNum(licenseNum: String): Future[Option[Pilot]] = findOneWhere(_.licenseNum === licenseNum)
}
