package com.jc.api.endpoint.user.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.jc.api.endpoint.user.application.{Session, UserService}
import com.jc.api.endpoint.user.UserId
import com.jc.api.model.BasicUserData
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions._
import com.softwaremill.session.{RefreshTokenStorage, SessionManager}

import scala.concurrent.ExecutionContext

trait SessionSupport {

  implicit def sessionManager: SessionManager[Session]

  implicit def refreshTokenStorage: RefreshTokenStorage[Session]

  implicit def ec: ExecutionContext

  def userService: UserService

  def userFromSession: Directive1[BasicUserData] = userIdFromSession.flatMap { userId =>
    onSuccess(userService.findById(userId)).flatMap {
      case None => reject(AuthorizationFailedRejection)
      case Some(user) => provide(user)
    }
  }

  def userIdFromSession: Directive1[UserId] = session(refreshable, usingCookies).flatMap {
    _.toOption match {
      case None => reject(AuthorizationFailedRejection)
      case Some(s) => provide(s.userId)
    }
  }
}
