package com.jc.api.model

import java.time.OffsetDateTime
import java.util.UUID
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

import com.jc.api.common.Utils
import com.jc.api.endpoint.user.{UserId, UserRoleId, UserStatusId}

/** Entity class storing rows of table Users
  *  @param id Database column id SqlType(uuid), PrimaryKey
  *  @param login Database column login SqlType(varchar)
  *  @param loginLowercase Database column login_lowercase SqlType(varchar)
  *  @param email Database column email SqlType(varchar)
  *  @param password Database column password SqlType(varchar)
  *  @param salt Database column salt SqlType(varchar)
  *  @param userStatusId Database column role_id SqlType(int4)
  *  @param createdOn Database column created_on SqlType(OffsetDateTime) */
case class User(id: java.util.UUID, login: String, loginLowercase: String, email: String, password: String, salt: String, userStatusId: Int, createdOn: OffsetDateTime)
object User {

  def withRandomUUID(
    login         : String,
    email         : String,
    plainPassword : String,
    salt          : String,
    roleId        : UserRoleId,
    createdOn     : OffsetDateTime
  ) = User(UUID.randomUUID(), login, login.toLowerCase, email, encryptPassword(plainPassword, salt), salt, roleId, createdOn)

  def encryptPassword(password: String, salt: String): String = {
    // 10k iterations takes about 10ms to encrypt a password on a 2013 MacBook
    val keySpec          = new PBEKeySpec(password.toCharArray, salt.getBytes, 10000, 128)
    val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val bytes            = secretKeyFactory.generateSecret(keySpec).getEncoded

    Utils.toHex(bytes)
  }

  def passwordsMatch(plainPassword: String, user: FullUserData): Boolean =
    Utils.constantTimeEquals(
      user.password,
      encryptPassword(plainPassword, user.salt)
    )
}

case class FullUserData(
  id              : UserId,
  login           : String,
  loginLowerCased : String,
  email           : String,
  password        : String,
  salt            : String,
  status          : UserStatus,
  roles           : Seq[UserRole],
  createdOn       : OffsetDateTime
)
object FullUserData {
  def apply(
    user          : User,
    userStatus    : UserStatus,
    userRoles     : Seq[UserRole]
  ): FullUserData =
    new FullUserData(user.id, user.login, user.loginLowercase, user.email, user.password, user.salt, userStatus, userRoles, user.createdOn)
}


case class BasicUserData(id: UserId, login: String, email: String, userStatus: UserStatus, roles: Seq[UserRole], createdOn: OffsetDateTime)

object BasicUserData {
  def fromFullUserData(user: FullUserData) = new BasicUserData(user.id, user.login, user.email, user.status, user.roles, user.createdOn)
}

/** Entity class storing rows of table UserRoles
  *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
  *  @param role Database column role SqlType(varchar) */
case class UserRole(id: Int, role: String)
object UserRole {
  val ROLE_ADMIN    : UserRole = UserRole(1, "Admin")
  val ROLE_CONSUMER : UserRole = UserRole(2, "Consumer")
  val ROLE_PROVIDER : UserRole = UserRole(3, "Provider")
  val ROLE_PILOT    : UserRole = UserRole(4, "Pilot")
  implicit def userRoleRow2UserRole(u: UserRole): UserRole = UserRole(u.id, u.role)
  implicit def userRole2UserRoleRow(u: UserRole): UserRole = UserRole(u.id, u.role)
}

/** Entity class storing rows of table UserStatus
  *  @param id Database column id SqlType(int4), PrimaryKey
  *  @param status Database column status SqlType(varchar) */
case class UserStatus(id: Int, status: String)
object UserStatus {
  val STATUS_ACTIVE   : UserStatus = UserStatus(1, "Active")
  val STATUS_INACTIVE : UserStatus = UserStatus(2, "Inactive")
  val STATUS_LOCKED   : UserStatus = UserStatus(3, "Locked")
  val STATUS_DELETE   : UserStatus = UserStatus(4, "Deleted")
  val DEFAULT_STATUS  : UserStatus = STATUS_INACTIVE
}