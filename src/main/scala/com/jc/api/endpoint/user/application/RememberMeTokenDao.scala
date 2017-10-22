package com.jc.api.endpoint.user.application

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.model.RememberMeToken
import com.jc.api.schema.SqlRememberMeSchema

import scala.concurrent.{ExecutionContext, Future}

class RememberMeTokenDao(protected val database: SqlDatabase)(implicit ec: ExecutionContext)
    extends SqlRememberMeSchema {

  import database._
  import database.profile.api._

  def findBySelector(selector: String): Future[Option[RememberMeToken]] =
    db.run(rememberMeTokens.filter(_.selector === selector).result).map(_.headOption)

  def add(data: RememberMeToken): Future[Unit] =
    db.run(rememberMeTokens += data).map(_ => ())

  def remove(selector: String): Future[Unit] =
    db.run(rememberMeTokens.filter(_.selector === selector).delete).map(_ => ())
}

