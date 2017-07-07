package com.jc.account.user.application

import java.time.{Instant, ZoneOffset}
import java.util.UUID

import com.jc.account.common.Utils
import com.jc.account.user.UserId
import com.jc.account.user.domain.{BasicUserData, User}

import scala.concurrent.{ExecutionContext, Future}

class UserService(
    userDao: UserDao
//    emailService: EmailService,
//    emailTemplatingEngine: EmailTemplatingEngine
)(implicit ec: ExecutionContext) {

  def findById(userId: UserId): Future[Option[BasicUserData]] =
    userDao.findBasicDataById(userId)

  def registerNewUser(login: String, email: String, password: String): Future[UserRegisterResult] = {
    def checkUserExistence(): Future[Either[UserRegisterResult, Unit]] = {
      val existingLoginFuture = userDao.findByLowerCasedLogin(login)
      val existingEmailFuture = userDao.findByEmail(email)

      for {
        existingLoginOpt <- existingLoginFuture
        existingEmailOpt <- existingEmailFuture
      } yield {

        def getMsg[String](a: Tuple2[Option[User], String]):Option[String] = a match {
          case Tuple2(Some(_), msg) => Some(msg)
          case Tuple2(_, msg) => None
        }

        val msgs = Seq((existingLoginOpt, "Login already in use!"), (existingEmailOpt, "E-mail already in use!")).map(getMsg);
        if (msgs.forall(_.isEmpty)) {
          Right((): Unit)
        } else {
          Left(UserRegisterResult.UserExists(msgs.head, msgs.last))
        }
      }
    }

    def registerValidData() = checkUserExistence().flatMap {
      case Left(result) => Future.successful(result)
      case Right(_) =>
        val salt          = Utils.randomString(128)
        val now           = Instant.now().atOffset(ZoneOffset.UTC)
        val userAddResult = userDao.add(User.withRandomUUID(login, email.toLowerCase, password, salt, now))
//        userAddResult.foreach { _ =>
//          val confirmationEmail = emailTemplatingEngine.registrationConfirmation(login)
//          emailService.scheduleEmail(email, confirmationEmail)
//        }
        userAddResult.map(_ => UserRegisterResult.Success)
    }

    UserRegisterValidator
      .validate(login, email, password)
      .fold(
        invalid => Future.successful(invalid),
        _ => registerValidData()
      )
  }

  def authenticate(login: String, nonEncryptedPassword: String): Future[Option[BasicUserData]] =
    userDao
      .findByLoginOrEmail(login)
      .map(userOpt => userOpt.filter(u => User.passwordsMatch(nonEncryptedPassword, u)).map(BasicUserData.fromUser))

  def changeLogin(userId: UUID, newLogin: String): Future[Either[String, Unit]] =
    userDao.findByLowerCasedLogin(newLogin).flatMap {
      case Some(_) => Future.successful(Left("Login is already taken"))
      case None    => userDao.changeLogin(userId, newLogin).map(Right(_))
    }

  def changeEmail(userId: UUID, newEmail: String): Future[Either[String, Unit]] =
    userDao.findByEmail(newEmail).flatMap {
      case Some(_) => Future.successful(Left("E-mail used by another user"))
      case None    => userDao.changeEmail(userId, newEmail).map(Right(_))
    }

  def changePassword(userId: UUID, currentPassword: String, newPassword: String): Future[Either[String, Unit]] =
    userDao.findById(userId).flatMap {
      case Some(u) =>
        if (User.passwordsMatch(currentPassword, u)) {
          userDao.changePassword(u.id, User.encryptPassword(newPassword, u.salt)).map(Right(_))
        } else Future.successful(Left("Current password is invalid"))

      case None => Future.successful(Left("User not found hence cannot change password"))
    }
}

sealed trait UserRegisterResult

object UserRegisterResult {

  case class InvalidData(login: Option[String], email: Option[String], password: Option[String]) extends UserRegisterResult

  case class UserExists(login: Option[String], email: Option[String]) extends UserRegisterResult

  case object Success extends UserRegisterResult

}

object UserRegisterValidator {
  private val ValidationOk = Right(():Unit)
  val MinLoginLength       = 3

  def validate(login: String, email: String, password: String): Either[UserRegisterResult, Unit] = {
    val loginEither = validLogin(login.trim)
    val emailEither = validEmail(email.trim)
    val passwordEither = validPassword(password.trim)

    def getMsg(either: Either[String, Unit]): Option[String] = either match {
      case Left(msg) => Some(msg);
      case _ => None;
    }

    val errorMsgs = Seq(loginEither, emailEither, passwordEither).map(getMsg)

    if (!errorMsgs.forall(_.isEmpty)) {
      Left(UserRegisterResult.InvalidData(errorMsgs(0), errorMsgs(1), errorMsgs(2)))
    } else {
      ValidationOk
    }
  }
//    for {
//      _ <- validLogin(login.trim).right
//      _ <- validEmail(email.trim).right
//      _ <- validPassword(password.trim).right
//    } yield ()

  private def validLogin(login: String) =
    if (login.length >= MinLoginLength) ValidationOk else Left("Login is too short!")

  private val emailRegex =
    """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  private def validEmail(email: String) =
    if (emailRegex.findFirstMatchIn(email).isDefined) ValidationOk else Left("Invalid e-mail!")

  private def validPassword(password: String) =
    if (password.nonEmpty) ValidationOk else Left("Password cannot be empty!")
}
