package com.jc.api.endpoint.user.application

import java.time.OffsetDateTime

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.endpoint.user.{UserId, UserRoleId, UserStatusId}
import com.jc.api.model._
import com.jc.api.schema.SqlAccountServiceSchema
import slick.model.ForeignKeyAction

import scala.concurrent.{ExecutionContext, Future}

class UserDao(protected val database: SqlDatabase)(implicit val ec: ExecutionContext) extends SqlUserSchema {

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

trait SqlUserSchema {
  protected val database: SqlDatabase

  import database._
  import database.profile.api._
  import slick.jdbc.{GetResult => GR}
  import slick.model.ForeignKeyAction

  import com.jc.api.model.RememberMeToken

  /** GetResult implicit for fetching RememberMeTokensRow objects using plain SQL queries */
  implicit def GetResultRememberMeTokensRow(
    implicit e0: GR[java.util.UUID],
    e1: GR[String],
    e2: GR[OffsetDateTime]
  ): GR[RememberMeToken] = GR { prs =>
    import prs._
    RememberMeToken.tupled((<<[java.util.UUID], <<[String], <<[String], <<[java.util.UUID], <<[OffsetDateTime]))
  }

  /** Table description of table REMEMBER_ME_TOKENS. Objects of this class serve as prototypes for rows in queries. */
  class RememberMeTokens(_tableTag: Tag) extends profile.api.Table[RememberMeToken](_tableTag, "REMEMBER_ME_TOKENS") {
    def * = (id, selector, tokenHash, userId, validTo) <> (RememberMeToken.tupled, RememberMeToken.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(selector), Rep.Some(tokenHash), Rep.Some(userId), Rep.Some(validTo)).shaped.<>(
      { r => import r._; _1.map(_ => RememberMeToken.tupled((_1.get, _2.get, _3.get, _4.get, _5.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(uuid), PrimaryKey */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id", O.PrimaryKey)
    /** Database column selector SqlType(varchar) */
    val selector: Rep[String] = column[String]("selector")
    /** Database column token_hash SqlType(varchar) */
    val tokenHash: Rep[String] = column[String]("token_hash")
    /** Database column user_id SqlType(uuid) */
    val userId: Rep[java.util.UUID] = column[java.util.UUID]("user_id")
    /** Database column valid_to SqlType(OffsetDateTime) */
    val validTo: Rep[OffsetDateTime] = column[OffsetDateTime]("valid_to")

    /** Foreign key referencing Users (database name REMEMBER_ME_TOKENS_user_id_fkey) */
    lazy val usersFk = foreignKey("REMEMBER_ME_TOKENS_user_id_fkey", userId, users)(
      r => r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade
    )
  }

  /** Collection-like TableQuery object for table RememberMeTokens */
  lazy val rememberMeTokens = new TableQuery(tag => new RememberMeTokens(tag))

  import com.jc.api.model.UserStatus

  /** GetResult implicit for fetching UserStatusRow objects using plain SQL queries */
  implicit def GetResultUserStatus(
    implicit e0: GR[Int],
    e1: GR[String]
  ): GR[UserStatus] = GR { prs =>
    import prs._
    (UserStatus.apply _).tupled((<<[Int], <<[String]))
  }

  /** Table description of table USER_STATUS. Objects of this class serve as prototypes for rows in queries. */
  class UserStatuses(_tableTag: Tag) extends profile.api.Table[UserStatus](_tableTag, "USER_STATUSES") {
    def * = (id, status) <> ((UserStatus.apply _).tupled, UserStatus.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(status)).shaped.<>(
      { r => import r._; _1.map(_ => (UserStatus.apply _).tupled((_1.get, _2.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(int4), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column status SqlType(varchar) */
    val status: Rep[String] = column[String]("status")
  }

  /** Collection-like TableQuery object for table UserStatus */
  lazy val userStatuses = new TableQuery(tag => new UserStatuses(tag))

  import com.jc.api.model.UserRole

  /** GetResult implicit for fetching UserRolesRow objects using plain SQL queries */
  implicit def GetResultUserRoles(implicit e0: GR[Int], e1: GR[String]): GR[UserRole] = GR { prs =>
    import prs._
    (UserRole.apply _).tupled((<<[Int], <<[String]))
  }

  /** Table description of table USER_ROLES. Objects of this class serve as prototypes for rows in queries. */
  class UserRoles(_tableTag: Tag) extends profile.api.Table[UserRole](_tableTag, "USER_ROLES") {
    def * = (id, role) <> ((UserRole.apply _).tupled, UserRole.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(role)).shaped.<>(
      { r => import r._; _1.map(_ => (UserRole.apply _).tupled((_1.get, _2.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column role SqlType(varchar) */
    val role: Rep[String] = column[String]("role")
  }

  /** Collection-like TableQuery object for table UserRoles */
  lazy val userRoles = new TableQuery(tag => new UserRoles(tag))

  import com.jc.api.model.User

  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(
    implicit e0: GR[java.util.UUID],
    e1: GR[String],
    e2: GR[OffsetDateTime],
    e3: GR[Int]
  ): GR[User] = GR { prs =>
    import prs._
    (User.apply _).tupled(
      (<<[java.util.UUID], <<[String], <<[String], <<[String], <<[String], <<[String], <<[Int], <<[OffsetDateTime])
    )
  }

  /** Table description of table USERS. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends profile.api.Table[User](_tableTag, "USERS") {
    def * = (id, login, loginLowercase, email, password, salt, userStatusId, createdOn) <> ((User.apply _).tupled, User.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(login), Rep.Some(loginLowercase), Rep.Some(email), Rep.Some(password), Rep.Some(
      salt
    ), Rep.Some(userStatusId), Rep.Some(createdOn)).shaped.<>(
      { r => import r._; _1.map(
        _ => (User.apply _).tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get))
      )
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(uuid), PrimaryKey */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id", O.PrimaryKey)
    /** Database column login SqlType(varchar) */
    val login: Rep[String] = column[String]("login")
    /** Database column login_lowercase SqlType(varchar) */
    val loginLowercase: Rep[String] = column[String]("login_lowercase")
    /** Database column email SqlType(varchar) */
    val email: Rep[String] = column[String]("email")
    /** Database column password SqlType(varchar) */
    val password: Rep[String] = column[String]("password")
    /** Database column salt SqlType(varchar) */
    val salt: Rep[String] = column[String]("salt")
    /** Database column user_status_id SqlType(int4) */
    val userStatusId: Rep[Int] = column[Int]("user_status_id")
    /** Database column created_on SqlType(OffsetDateTime) */
    val createdOn: Rep[OffsetDateTime] = column[OffsetDateTime]("created_on")

    /** Foreign key referencing UserRoles (database name USER_ROLE_MAPPINGS_role_id_fkey) */
    lazy val userStatusFk = foreignKey("USERS_user_status_id_fkey", userStatusId, userStatuses)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table Users */
  lazy val users = new TableQuery(tag => new Users(tag))

  /** Entity class storing rows of table UserRoleMappings
    *
    * @param userId Database column user_id SqlType(uuid)
    * @param roleId Database column role_id SqlType(int4) */
  case class UserRoleMapping(userId: java.util.UUID, roleId: Int)

  /** GetResult implicit for fetching UserRoleMappingsRow objects using plain SQL queries */
  implicit def GetResultUserRoleMappingsRow(implicit e0: GR[java.util.UUID], e1: GR[Int]): GR[UserRoleMapping] = GR {
    prs =>
      import prs._
      UserRoleMapping.tupled((<<[java.util.UUID], <<[Int]))
  }

  /** Table description of table USER_ROLE_MAPPINGS. Objects of this class serve as prototypes for rows in queries. */
  class UserRoleMappings(_tableTag: Tag) extends profile.api.Table[UserRoleMapping](_tableTag, "USER_ROLE_MAPPINGS") {
    def * = (userId, roleId) <> (UserRoleMapping.tupled, UserRoleMapping.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(roleId)).shaped.<>(
      { r => import r._; _1.map(_ => UserRoleMapping.tupled((_1.get, _2.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column user_id SqlType(uuid) */
    val userId: Rep[java.util.UUID] = column[java.util.UUID]("user_id")
    /** Database column role_id SqlType(int4) */
    val roleId: Rep[Int] = column[Int]("role_id")

    /** Foreign key referencing Users (database name USER_ROLE_MAPPINGS_user_id_fkey) */
    lazy val usersFk = foreignKey("USER_ROLE_MAPPINGS_user_id_fkey", userId, users)(
      r => r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade
    )
    /** Foreign key referencing UserRoles (database name USER_ROLE_MAPPINGS_role_id_fkey) */
    lazy val userRolesFk = foreignKey("USER_ROLE_MAPPINGS_role_id_fkey", roleId, userRoles)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table UserRoleMappings */
  lazy val userRoleMappings = new TableQuery(tag => new UserRoleMappings(tag))
}
