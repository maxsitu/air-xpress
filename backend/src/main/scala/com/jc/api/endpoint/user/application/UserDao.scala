package com.jc.api.endpoint.user.application

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.endpoint.user.{UserId, UserRoleId, UserStatusId}
import com.jc.api.model._
import com.jc.api.schema.SqlAccountServiceSchema

import scala.concurrent.{ExecutionContext, Future}

class UserDao(protected val database: SqlDatabase)(implicit val ec: ExecutionContext) extends SqlAccountServiceSchema {

  import database._
  import database.profile.api._

  private def findOneWhere(condition: Users => Rep[Boolean]) = db.run(users.filter(condition).result.headOption)

  def addUser(user: User): Future[Unit] = db.run(users += user).map(_ => ())
  def findUserById(userId: UserId): Future[Option[User]] = findOneWhere(_.id === userId)
  def findUserByEmail(email: String): Future[Option[User]] = findOneWhere(_.email.toLowerCase === email.toLowerCase)
  def findUserStatusById(statusId: UserStatusId): Future[Option[UserStatus]] = db.run(userStatuses.filter(s => s.id === statusId).result.headOption)
  def findUserRolesById(roleId: UserRoleId): Future[Seq[UserRole]] = db.run(userRoles.filter(_.id === roleId).result)
  def findUserRolesByUserId(userId: UserId): Future[Seq[UserRole]] = {
    db.run(
      (for{
        ((u, m), r) <- users.filter(u => u.id === userId) join userRoleMappings on ((u,m) => u.id === m.userId) join userRoles on ((um, r) => um._2.roleId === r.id)
      } yield r).result
    )
  }

  def changeUserStatus(userId: UserId, userStatus: UserStatus): Future[Unit] = db.run(
    users.filter(_.id === userId).map(_.userStatusId).update(userStatus.id).map(_ => ())
  )

  private def enrichUserWithStatusAndRoles(user: Option[User]): Future[Option[FullUserData]] = user match {
    case Some(u: User) => {
      for {
        status <- findUserStatusById(u.userStatusId)
        roles  <- findUserRolesByUserId(u.id)
      } yield Some(FullUserData(u, status.getOrElse(UserStatus.DEFAULT_STATUS), roles))
    }
    case None => Future.successful(None)
  }

  def findFullUserDataById(id: UserId): Future[Option[FullUserData]] = findUserById(id).flatMap(enrichUserWithStatusAndRoles)

  def findFullUserDataByEmail(email: String): Future[Option[FullUserData]] = findUserByEmail(email).flatMap(enrichUserWithStatusAndRoles)

  def findBasicUserDataById(userId: UserId): Future[Option[BasicUserData]] = findUserById(userId).flatMap(enrichUserWithStatusAndRoles).map(_.map(BasicUserData.fromFullUserData(_)))

  def findUserByLowerCasedLogin(login: String): Future[Option[User]] = findOneWhere(_.loginLowercase === login.toLowerCase)

  def findFullUserDataByLowerCasedLogin(login: String): Future[Option[FullUserData]] = findUserByLowerCasedLogin(login).flatMap(enrichUserWithStatusAndRoles)

  def findByLoginOrEmail(loginOrEmail: String): Future[Option[FullUserData]] =
    findFullUserDataByLowerCasedLogin(loginOrEmail).flatMap(
      userOpt => userOpt.map(user => Future.successful(Some(user))).getOrElse(findFullUserDataByEmail(loginOrEmail))
    )

  def changePassword(userId: UserId, newPassword: String, newSalt: String): Future[Unit] =
    db.run(users.filter(_.id === userId).map(u => (u.password, u.salt)).update((newPassword, newSalt))).map(_ => ())

  def changeLogin(userId: UserId, newLogin: String): Future[Unit] = {
    val action = users
      .filter(_.id === userId)
      .map { user =>
        (user.login, user.loginLowercase)
      }
      .update((newLogin, newLogin.toLowerCase))
    db.run(action).map(_ => ())
  }

  def changeEmail(userId: UserId, newEmail: String): Future[Unit] =
    db.run(users.filter(_.id === userId).map(_.email).update(newEmail)).map(_ => ())
}
