package com.jc.api.endpoint.user.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{AuthorizationFailedRejection, ValidationRejection}
import akka.http.scaladsl.server.Directives._
import com.jc.api.common.Utils
import com.jc.api.common.api.RoutesSupport
import com.jc.api.endpoint.user.application.UserRegisterResult.{InvalidData, UserExists}
import com.jc.api.endpoint.user.application.{Session, UserRegisterResult, UserService}
import com.jc.api.model.{BasicUserData, UserRole, UserStatus}
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions._
import com.typesafe.scalalogging.StrictLogging
import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor}
import io.circe.generic.auto._

import scala.concurrent.Future
import scala.util.{Success, Try}

trait UsersRoutes extends RoutesSupport with StrictLogging with SessionSupport {

  def userService: UserService

  implicit val basicUserDataCbs = CanBeSerialized[BasicUserData]
  implicit val invalidDataCbs = CanBeSerialized[InvalidData]
  implicit val userExistsCbs = CanBeSerialized[UserExists]

  val usersRoutes = pathPrefix("users") {
    path("logout") {
      get {
        userIdFromSession { _ =>
          invalidateSession(refreshable, usingCookies) {
            completeOk
          }
        }
      }
    } ~
      path("register") {
        post {
          entity(as[RegistrationInput]) { in =>
            onSuccess(userService.registerNewUser(in.loginEscaped, in.email, in.password, UserRole.ROLE_CONSUMER.id)) {
              case msg: UserRegisterResult.InvalidData => complete(StatusCodes.BadRequest, msg)
              case msg: UserRegisterResult.UserExists => complete(StatusCodes.Conflict, msg)
              case UserRegisterResult.Success => complete("success")
            }
          }
        }
      } ~
      path("activate" / JavaUUID) { userId =>
        post {
          onComplete(userService.activateUser(userId)) {
            case Success(_) => complete("success")
          }
        }
      } ~
      path("changePassword") {
        post {
          userFromSession { user =>
            entity(as[ChangePasswordInput]) { in =>
              onSuccess(userService.changePassword(user.id, in.currentPassword, in.newPassword)) {
                case Left(msg) => complete(StatusCodes.Forbidden, msg)
                case Right(_) => completeOk
              }
            }
          }
        }
      } ~
      post {
        pathPrefix("addRoleAdmin") {
          path("userId" / JavaUUID) { userId =>
            onSuccess(userService.addUserRoles(userId, Seq(UserRole.ROLE_ADMIN))) {
              completeOk
            }
          }
        } ~
          pathPrefix("addRoleConsumer") {
            path("userId" / JavaUUID) { userId =>
              onSuccess(userService.addUserRoles(userId, Seq(UserRole.ROLE_CONSUMER))) {
                completeOk
              }
            }
          } ~
          pathPrefix("addRoleProvider") {
            path("userId" / JavaUUID) { userId =>
              onSuccess(userService.addUserRoles(userId, Seq(UserRole.ROLE_PROVIDER))) {
                completeOk
              }
            }
          } ~
          pathPrefix("addRolePilot") {
            path("userId" / JavaUUID) { userId =>
              onSuccess(userService.addUserRoles(userId, Seq(UserRole.ROLE_PILOT))) {
                completeOk
              }
            }
          }
      } ~
      pathEnd {
        post {
          entity(as[LoginInput]) { in =>
            onSuccess(userService.authenticate(in.login, in.password)) {
              case None => reject(AuthorizationFailedRejection)
              case Some(user) =>
                user.userStatus match {
                  case UserStatus.STATUS_ACTIVE => {
                    val session = Session(user.id)
                    (if (in.rememberMe.getOrElse(false)) {
                      setSession(refreshable, usingCookies, session)
                    } else {
                      setSession(oneOff, usingCookies, session)
                    }) {
                      complete(user)
                    }
                  }
                  case UserStatus.STATUS_INACTIVE =>
                    reject(ValidationRejection(UserConstants.MSG_INACTIVE_USER))
                  case UserStatus.STATUS_LOCKED =>
                    reject(ValidationRejection(UserConstants.MSG_LOCKED_USER))
                  case UserStatus.STATUS_DELETE =>
                    reject(ValidationRejection(UserConstants.MSG_DELETED_USER))
                  case _ =>
                    reject(ValidationRejection(UserConstants.MSG_INACTIVE_USER))
                }
            }
          }
        } ~
        get {
          userFromSession { user =>
            complete(user)
          }
        } ~
        patch {
          userIdFromSession { userId =>
            entity(as[PatchUserInput]) { in =>
              val updateAction = (in.login, in.email) match {
                case (Some(login), _) => userService.changeLogin(userId, login)
                case (_, Some(email)) => userService.changeEmail(userId, email)
                case _ => Future.successful(Left("You have to provide new login or email"))
              }

              onSuccess(updateAction) {
                case Left(msg) => complete(StatusCodes.Conflict, msg)
                case Right(_) => completeOk
              }
            }
          }
        }
      }
  }
}

case class RegistrationInput(login: String, email: String, password: String) {
  def loginEscaped = Utils.escapeHtml(login)
}

case class ChangePasswordInput(currentPassword: String, newPassword: String)

case class LoginInput(login: String, password: String, rememberMe: Option[Boolean]) {
  implicit object LoginInputDecoder extends Decoder[LoginInput] {
    override def apply(c: HCursor): Result[LoginInput] =
      for {
        login <- c.get[String]("login")
        passwd <- c.get[String]("password")
        rememberMe <- c.get[Option[Boolean]]("rememberMe")
      } yield LoginInput(login, passwd, rememberMe)
  }
}

case class PatchUserInput(login: Option[String], email: Option[String])
