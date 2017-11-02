package com.jc.api.endpoint.user.application

import java.time.{Instant, ZoneOffset}
import java.util.UUID

import com.jc.api.common.Utils
import com.jc.api.common.crypto.{PasswordHashing, Salt}
import com.jc.api.email.application.{EmailService, EmailTemplatingEngine}
import com.jc.api.endpoint.user.{UserId, UserRoleId}
import com.jc.api.model.{BasicUserData, FullUserData, User, UserStatus}

import scala.concurrent.{ExecutionContext, Future}

class UserService(
    userDao: UserDao,
    emailService: EmailService,
    emailTemplatingEngine: EmailTemplatingEngine,
    passwordHashing: PasswordHashing
)(implicit ec: ExecutionContext) {

  def findById(userId: UserId): Future[Option[BasicUserData]] =
    userDao.findBasicUserDataById(userId)

  def registerNewUser(login: String, email: String, password: String, roleId: UserRoleId): Future[UserRegisterResult] = {
    def checkUserExistence(): Future[Either[UserRegisterResult, Unit]] = {
      val existingLoginFuture = userDao.findUserByLowerCasedLogin(login)
      val existingEmailFuture = userDao.findUserByEmail(email)

      for {
        existingLoginOpt <- existingLoginFuture
        existingEmailOpt <- existingEmailFuture
      } yield {

        def getMsg[String](a: Tuple2[Option[User], String]):Option[String] = a match {
          case Tuple2(Some(_), msg) => Some(msg)
          case Tuple2(_, msg) => None
        }

        val msgs = Seq((existingLoginOpt, "Login already in use!"), (existingEmailOpt, "E-mail already in use!")).map(getMsg);
        if (msgs.exists(_.isDefined)) {
          Left(UserRegisterResult.UserExists(msgs.head, msgs.last))
        } else {
          Right((): Unit)
        }
      }
    }

    def registerValidData() = checkUserExistence().flatMap {
      case Left(result) => Future.successful(result)
      case Right(_) =>
        val salt          = Salt.newSalt()
        val now           = Instant.now().atOffset(ZoneOffset.UTC)
        val userAddResult = userDao.addUser(User.withRandomUUID(login, email.toLowerCase, password, salt, roleId, now))
        userAddResult.foreach { _ =>
          val confirmationEmail = emailTemplatingEngine.registrationConfirmation(login)
          emailService.scheduleEmail(email, confirmationEmail)
        }
        userAddResult.map(_ => UserRegisterResult.Success)
    }

    UserRegisterValidator
      .validate(login, email, password)
      .fold(
        invalid => Future.successful(invalid),
        _ => registerValidData()
      )
  }

  def activateUser(userId: UserId): Future[Unit] = {
    userDao.changeUserStatus(userId, UserStatus.STATUS_ACTIVE)
  }
  /**
    * Compare password with encrypted password stored in DB. Returning basic info of user only
    * if encrypted passwords match
    * @param login                  User's login name or email
    * @param nonEncryptedPassword   plain password
    * @return
    */
  def authenticate(login: String, nonEncryptedPassword: String): Future[Option[BasicUserData]] =
    userDao
      .findByLoginOrEmail(login)
      .map(_.filter(u => passwordHashing.verifyPassword(u.password, nonEncryptedPassword, u.salt)))
      .flatMap {
        case Some(u) => rehashIfRequired(u, nonEncryptedPassword)
        case None    => Future.successful(None)
      }
      .map(_.map(BasicUserData.fromFullUserData))

  /**
    * Some hash algorithms (like Argon2) can use parameters to affect how they work.
    * Typically these parameters are stored along the hash and they can change to speed up or slow down hashing
    * depending on needs and security status.
    *
    * It sounds like a good idea to check whether hashing parameters were changed recently after user successfully logs in
    * and if so, rehash user password using those new parameters. That way all user passwords are stored with up to date
    * security settings.
    */
  private def rehashIfRequired(u: FullUserData, password: String): Future[Option[FullUserData]] =
    if (passwordHashing.requiresRehashing(u.password)) {
      val newSalt     = Salt.newSalt()
      val newPassword = passwordHashing.hashPassword(password, newSalt)
      userDao.changePassword(u.id, newPassword, newSalt).map(_ => Some(u.copy(password = newPassword, salt = newSalt)))
    } else {
      Future.successful(Some(u))
    }

  def changeLogin(userId: UUID, newLogin: String): Future[Either[String, Unit]] =
    userDao.findUserByLowerCasedLogin(newLogin).flatMap {
      case Some(_) => Future.successful(Left("Login is already taken"))
      case None    => userDao.changeLogin(userId, newLogin).map(Right(_))
    }

  def changeEmail(userId: UUID, newEmail: String): Future[Either[String, Unit]] =
    userDao.findUserByEmail(newEmail).flatMap {
      case Some(_) => Future.successful(Left("E-mail used by another user"))
      case None    => userDao.changeEmail(userId, newEmail).map(Right(_))
    }

  def changePassword(userId: UUID, currentPassword: String, newPassword: String): Future[Either[String, Unit]] =
    userDao.findFullUserDataById(userId).flatMap {
      case Some(u) =>
        if (passwordHashing.verifyPassword(u.password, currentPassword, u.salt)) {
          val salt = Salt.newSalt()
          userDao.changePassword(u.id, passwordHashing.hashPassword(newPassword, salt), salt).map(Right(_))
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
      case Left(msg) => Some(msg)
      case _ => None
    }

    val errorMsgs = Seq(loginEither, emailEither, passwordEither).map(getMsg)

    if (!errorMsgs.forall(_.isEmpty)) {
      Left(UserRegisterResult.InvalidData(errorMsgs(0), errorMsgs(1), errorMsgs(2)))
    } else {
      ValidationOk
    }
  }

  private def validLogin(login: String) =
    if (login.length >= MinLoginLength) ValidationOk else Left("Login is too short!")

  private val emailRegex =
    """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  private def validEmail(email: String) =
    if (emailRegex.findFirstMatchIn(email).isDefined) ValidationOk else Left("Invalid e-mail!")

  private def validPassword(password: String) =
    if (password.nonEmpty) ValidationOk else Left("Password cannot be empty!")
}
