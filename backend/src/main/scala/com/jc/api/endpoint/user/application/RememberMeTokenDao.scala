package com.jc.api.endpoint.user.application

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.model.RememberMeToken

import scala.concurrent.{ExecutionContext, Future}

class RememberMeTokenDao(protected val database: SqlDatabase)(implicit ec: ExecutionContext)
    extends SqlUserSchema {

  import database._
  import database.profile.api._

  def findBySelector(selector: String): Future[Option[RememberMeToken]] =
    db.run(rememberMeTokens.filter(_.selector === selector).result.headOption)

  def add(data: RememberMeToken): Future[Unit] =
    db.run(rememberMeTokens += data).map(_ => ())

  def remove(selector: String): Future[Unit] =
    db.run(rememberMeTokens.filter(_.selector === selector).delete).map(_ => ())
}

