package com.jc.api.schema

import java.time.OffsetDateTime

import com.jc.api.common.sql.SqlDatabase
import com.jc.api.model.{BasicUserData, ConsumerAsk, Pilot}
import slick.jdbc.JdbcProfile

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
//object Tables extends {
//  val profile = slick.jdbc.PostgresProfile
//} with SqlAccountServiceSchema

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait SqlAccountServiceSchema {
  protected val database: SqlDatabase

  import database._
  import database.profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  val profile: JdbcProfile = database.profile
  /** DDL for all tables. Call .create to execute. */
  lazy val schema: database.profile.SchemaDescription = Array(
    consumeOrders.schema,
    consumerAsks.schema,
    consumerBids.schema,
    flightPlans.schema,
    flightSteps.schema,
    locations.schema,
    passwordResetCodes.schema,
    pilots.schema,
    planeProviderMappings.schema,
    planeProviders.schema,
    planes.schema,
    provideOrders.schema,
    providerAsks.schema,
    providerBids.schema,
    rememberMeTokens.schema,
    userRoleMappings.schema,
    userRoles.schema,
    users.schema
  ).reduceLeft(_ ++ _)

  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema



  /** GetResult implicit for fetching ConsumerAsksRow objects using plain SQL queries */
  implicit def GetResultConsumerAsksRow(
    implicit e0: GR[Long],
    e1: GR[Option[Long]],
    e2: GR[java.util.UUID],
    e3: GR[Int],
    e4: GR[Option[Boolean]],
    e5: GR[OffsetDateTime]): GR[ConsumerAsk] = GR
  {
    prs =>
      import prs._
      ConsumerAsk.tupled(
        (<<[Long], <<?[Long], <<[java.util.UUID], <<[Int], <<?[Boolean], <<[OffsetDateTime], <<[OffsetDateTime])
      )
  }

  /** Table description of table CONSUMER_ASKS. Objects of this class serve as prototypes for rows in queries. */
  class ConsumerAsks(_tableTag: Tag) extends profile.api.Table[ConsumerAsk](_tableTag, "CONSUMER_ASKS") {
    def * = (id, planId, consumerId, passengers, active, askedOn, modifiedOn) <> (ConsumerAsk.tupled, ConsumerAsk.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), planId, Rep.Some(consumerId), Rep.Some(passengers), active, Rep.Some(askedOn), Rep.Some(
      modifiedOn
    )).shaped.<>(
      { r => import r._; _1.map(_ => ConsumerAsk.tupled((_1.get, _2, _3.get, _4.get, _5, _6.get, _7.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column plan_id SqlType(int8), Default(None) */
    val planId: Rep[Option[Long]] = column[Option[Long]]("plan_id", O.Default(None))
    /** Database column consumer_id SqlType(uuid) */
    val consumerId: Rep[java.util.UUID] = column[java.util.UUID]("consumer_id")
    /** Database column passengers SqlType(int8) */
    val passengers: Rep[Int] = column[Int]("passengers")
    /** Database column active SqlType(bool), Default(Some(true)) */
    val active: Rep[Option[Boolean]] = column[Option[Boolean]]("active", O.Default(Some(true)))
    /** Database column asked_on SqlType(OffsetDateTime) */
    val askedOn: Rep[OffsetDateTime] = column[OffsetDateTime]("asked_on")
    /** Database column modified_on SqlType(OffsetDateTime) */
    val modifiedOn: Rep[OffsetDateTime] = column[OffsetDateTime]("modified_on")

    /** Foreign key referencing FlightPlans (database name CONSUMER_ASKS_plan_id_fkey) */
    lazy val flightPlansFk = foreignKey("CONSUMER_ASKS_plan_id_fkey", planId, flightPlans)(
      r => Rep.Some(r.id), onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
    /** Foreign key referencing Users (database name CONSUMER_ASKS_consumer_id_fkey) */
    lazy val usersFk = foreignKey("CONSUMER_ASKS_consumer_id_fkey", consumerId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table ConsumerAsks */
  lazy val consumerAsks = new TableQuery(tag => new ConsumerAsks(tag))

  /** Entity class storing rows of table ConsumeOrders
    *
    * @param consumerAskId Database column consumer_ask_id SqlType(int8)
    * @param providerBidId Database column provider_bid_id SqlType(int8)
    * @param confirmed     Database column confirmed SqlType(bool), Default(Some(false))
    * @param createdOn     Database column created_on SqlType(OffsetDateTime)
    * @param modifiedOn    Database column modified_on SqlType(OffsetDateTime) */
  case class ConsumeOrdersRow(
    consumerAskId: Long,
    providerBidId: Long,
    confirmed: Option[Boolean] = Some(false),
    createdOn: OffsetDateTime,
    modifiedOn: OffsetDateTime
  )

  /** GetResult implicit for fetching ConsumeOrdersRow objects using plain SQL queries */
  implicit def GetResultConsumeOrdersRow(
    implicit e0: GR[Long],
    e1: GR[Option[Boolean]],
    e2: GR[OffsetDateTime]
  ): GR[ConsumeOrdersRow] = GR { prs =>
    import prs._
    ConsumeOrdersRow.tupled(
      (<<[Long], <<[Long], <<?[Boolean], <<[OffsetDateTime], <<[OffsetDateTime])
    )
  }

  /** Table description of table CONSUME_ORDERS. Objects of this class serve as prototypes for rows in queries. */
  class ConsumeOrders(_tableTag: Tag) extends profile.api.Table[ConsumeOrdersRow](_tableTag, "CONSUME_ORDERS") {

    def * = (consumerAskId, providerBidId, confirmed, createdOn, modifiedOn) <> (ConsumeOrdersRow.tupled, ConsumeOrdersRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (
      Rep.Some(consumerAskId), Rep.Some(providerBidId), confirmed, Rep.Some(createdOn), Rep.Some(modifiedOn)
    ).shaped.<>(
      { r => import r._; _1.map(_ => ConsumeOrdersRow.tupled((_1.get, _2.get, _3, _4.get, _5.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column consumer_ask_id SqlType(int8) */
    val consumerAskId: Rep[Long] = column[Long]("consumer_ask_id")
    /** Database column provider_bid_id SqlType(int8) */
    val providerBidId: Rep[Long] = column[Long]("provider_bid_id")
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val confirmed: Rep[Option[Boolean]] = column[Option[Boolean]]("confirmed", O.Default(Some(false)))
    /** Database column created_on SqlType(OffsetDateTime) */
    val createdOn: Rep[OffsetDateTime] = column[OffsetDateTime]("created_on")
    /** Database column modified_on SqlType(OffsetDateTime) */
    val modifiedOn: Rep[OffsetDateTime] = column[OffsetDateTime]("modified_on")

    /** Foreign key referencing ConsumerAsks (database name CONSUME_ORDERS_consumer_ask_id_fkey) */
    lazy val consumerAsksFk = foreignKey("CONSUME_ORDERS_consumer_ask_id_fkey", consumerAskId, consumerAsks)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
    /** Foreign key referencing ProviderBids (database name CONSUME_ORDERS_provider_bid_id_fkey) */
    lazy val providerBidsFk = foreignKey("CONSUME_ORDERS_provider_bid_id_fkey", providerBidId, providerBids)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table ConsumeOrders */
  lazy val consumeOrders = new TableQuery(tag => new ConsumeOrders(tag))

  import com.jc.api.model.ConsumerBid

  /** GetResult implicit for fetching ConsumerBidsRow objects using plain SQL queries */
  implicit def GetResultConsumerBidsRow(
    implicit e0: GR[Long],
    e1: GR[java.util.UUID],
    e2: GR[Option[Boolean]],
    e3: GR[OffsetDateTime],
    e4: GR[Int]
  ): GR[ConsumerBid] = GR { prs =>
      import prs._
      ConsumerBid.tupled(
        (<<[Long], <<[Long], <<[java.util.UUID], <<[Int], <<?[Boolean], <<[Boolean], <<[Boolean], <<[Boolean], <<[OffsetDateTime], <<[OffsetDateTime])
      )
  }

  /** Table description of table CONSUMER_BIDS. Objects of this class serve as prototypes for rows in queries. */
  class ConsumerBids(_tableTag: Tag) extends profile.api.Table[ConsumerBid](_tableTag, "CONSUMER_BIDS") {

    def * = (id, providerAskId, bidderId, passengers, active, confirmed, charged, refunded, createdOn, modifiedOn) <> (ConsumerBid.tupled, ConsumerBid.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(providerAskId), Rep.Some(bidderId), Rep.Some(passengers
    ), active, confirmed, charged, refunded, Rep.Some(createdOn), Rep.Some(modifiedOn)).shaped.<>(
      { r => import r._; _1.map(
        _ => ConsumerBid.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6, _7, _8, _9.get, _10.get))
      )
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column provider_ask_id SqlType(int8) */
    val providerAskId: Rep[Long] = column[Long]("provider_ask_id")
    /** Database column bidder_id SqlType(uuid) */
    val bidderId: Rep[java.util.UUID] = column[java.util.UUID]("bidder_id")
    /** Database column passengers SqlType(int8) */
    val passengers: Rep[Int] = column[Int]("passengers")
    /** Database column active SqlType(bool), Default(Some(true)) */
    val active: Rep[Option[Boolean]] = column[Option[Boolean]]("active", O.Default(Some(true)))
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val confirmed: Rep[Boolean] = column[Boolean]("confirmed", O.Default(false))
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val charged: Rep[Boolean] = column[Boolean]("charged", O.Default(false))
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val refunded: Rep[Boolean] = column[Boolean]("refunded", O.Default(false))
    /** Database column created_on SqlType(OffsetDateTime) */
    val createdOn: Rep[OffsetDateTime] = column[OffsetDateTime]("created_on")
    /** Database column modified_on SqlType(OffsetDateTime) */
    val modifiedOn: Rep[OffsetDateTime] = column[OffsetDateTime]("modified_on")

    /** Foreign key referencing ProviderAsks (database name CONSUMER_BIDS_provider_ask_id_fkey) */
    lazy val providerAsksFk = foreignKey("CONSUMER_BIDS_provider_ask_id_fkey", providerAskId, providerAsks)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
    /** Foreign key referencing Users (database name CONSUMER_BIDS_bidder_id_fkey) */
    lazy val usersFk = foreignKey("CONSUMER_BIDS_bidder_id_fkey", bidderId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table ConsumerBids */
  lazy val consumerBids = new TableQuery(tag => new ConsumerBids(tag))

  import com.jc.api.model.FlightPlan

  /** GetResult implicit for fetching FlightPlan objects using plain SQL queries */
  implicit def GetResultFlightPlansRow(
    implicit e0: GR[Long],
    e1: GR[Int],
    e2: GR[OffsetDateTime]): GR[FlightPlan] = GR { prs =>
      import prs._
    FlightPlan.tupled(
        (<<[Long], <<[Int], <<[OffsetDateTime], <<[OffsetDateTime], <<[OffsetDateTime])
      )
  }

  /** Table description of table FLIGHT_PLANS. Objects of this class serve as prototypes for rows in queries. */
  class FlightPlans(_tableTag: Tag) extends profile.api.Table[FlightPlan](_tableTag, "FLIGHT_PLANS") {
    def * = (id, passengerNum, startTime, endTime, modifiedOn) <> (FlightPlan.tupled, FlightPlan.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(passengerNum), Rep.Some(startTime), Rep.Some(endTime), Rep.Some(modifiedOn
    )).shaped.<>(
      { r => import r._; _1.map(_ => FlightPlan.tupled((_1.get, _2.get, _3.get, _4.get, _5.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column passenger_num SqlType(int) */
    def passengerNum = column[Int]("passenger_num")
    /** Database column start_time SqlType(OffsetDateTime), Default(None) */
    val startTime: Rep[OffsetDateTime] = column[OffsetDateTime]("start_time")
    /** Database column end_time SqlType(OffsetDateTime), Default(None) */
    val endTime: Rep[OffsetDateTime] = column[OffsetDateTime]("end_time")
    /** Database column modified_on SqlType(OffsetDateTime) */
    val modifiedOn: Rep[OffsetDateTime] = column[OffsetDateTime]("modified_on")
  }

  /** Collection-like TableQuery object for table FlightPlans */
  lazy val flightPlans = new TableQuery(tag => new FlightPlans(tag))

  import com.jc.api.model.FlightStep

  /** GetResult implicit for fetching FlightStepsRow objects using plain SQL queries */
  implicit def GetResultFlightStepsRow(
    implicit e0: GR[Long],
    e1: GR[Int],
    e2: GR[OffsetDateTime]): GR[FlightStep] = GR { prs =>
      import prs._
    FlightStep.tupled((<<[Long], <<[Long], <<[Int], <<[Int], <<[OffsetDateTime], <<[OffsetDateTime]))
  }

  /** Table description of table FLIGHT_STEPS. Objects of this class serve as prototypes for rows in queries. */
  class FlightSteps(_tableTag: Tag) extends profile.api.Table[FlightStep](_tableTag, "FLIGHT_STEPS") {
    def * = (id, planId, fromLocationId, toLocationId, fromTime, toTime) <> (FlightStep.tupled, FlightStep.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(planId), Rep.Some(fromLocationId), Rep.Some(toLocationId), Rep.Some(fromTime
    ), Rep.Some(toTime)).shaped.<>(
      { r => import r._; _1.map(_ => FlightStep.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column plan_id SqlType(int8) */
    val planId: Rep[Long] = column[Long]("plan_id")
    /** Database column from_location_id SqlType(int4) */
    val fromLocationId: Rep[Long] = column[Long]("from_location_id")
    /** Database column to_location_id SqlType(int4) */
    val toLocationId: Rep[Long] = column[Long]("to_location_id")
    /** Database column from_time SqlType(OffsetDateTime) */
    val fromTime: Rep[OffsetDateTime] = column[OffsetDateTime]("from_time")
    /** Database column to_time SqlType(OffsetDateTime) */
    val toTime: Rep[OffsetDateTime] = column[OffsetDateTime]("to_time")

    /** Foreign key referencing FlightPlans (database name FLIGHT_STEPS_plan_id_fkey) */
    lazy val flightPlansFk = foreignKey("FLIGHT_STEPS_plan_id_fkey", planId, flightPlans)(
      r => r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade
    )
    /** Foreign key referencing Locations (database name FLIGHT_STEPS_from_location_id_fkey) */
    lazy val locationsFk2 = foreignKey("FLIGHT_STEPS_from_location_id_fkey", fromLocationId, locations)(
      r => r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade
    )
    /** Foreign key referencing Locations (database name FLIGHT_STEPS_to_location_id_fkey) */
    lazy val locationsFk3 = foreignKey("FLIGHT_STEPS_to_location_id_fkey", toLocationId, locations)(
      r => r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade
    )
  }

  /** Collection-like TableQuery object for table FlightSteps */
  lazy val flightSteps = new TableQuery(tag => new FlightSteps(tag))

  /** Entity class storing rows of table Locations
    *
    * @param id     Database column id SqlType(serial), AutoInc, PrimaryKey
    * @param code   Database column code SqlType(varchar)
    * @param name   Database column name SqlType(varchar)
    * @param geoLat Database column geo_lat SqlType(float8)
    * @param geoLon Database column geo_lon SqlType(float8) */
  case class LocationsRow(
    id: Long,
    code: String,
    name: String,
    geoLat: Double,
    geoLon: Double
  )

  /** GetResult implicit for fetching LocationsRow objects using plain SQL queries */
  implicit def GetResultLocationsRow(
    implicit e0: GR[Long],
    e1: GR[String],
    e2: GR[Double]): GR[LocationsRow] = GR { prs =>
      import prs._
      LocationsRow.tupled((<<[Int], <<[String], <<[String], <<[Double], <<[Double]))
  }

  /** Table description of table LOCATIONS. Objects of this class serve as prototypes for rows in queries. */
  class Locations(_tableTag: Tag) extends profile.api.Table[LocationsRow](_tableTag, "LOCATIONS") {
    def * = (id, code, name, geoLat, geoLon) <> (LocationsRow.tupled, LocationsRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(code), Rep.Some(name), Rep.Some(geoLat), Rep.Some(geoLon)).shaped.<>(
      { r => import r._; _1.map(_ => LocationsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column code SqlType(varchar) */
    val code: Rep[String] = column[String]("code")
    /** Database column name SqlType(varchar) */
    val name: Rep[String] = column[String]("name")
    /** Database column geo_lat SqlType(float8) */
    val geoLat: Rep[Double] = column[Double]("geo_lat")
    /** Database column geo_lon SqlType(float8) */
    val geoLon: Rep[Double] = column[Double]("geo_lon")
  }

  /** Collection-like TableQuery object for table Locations */
  lazy val locations = new TableQuery(tag => new Locations(tag))

  /** Entity class storing rows of table PasswordResetCodes
    *
    * @param id      Database column id SqlType(uuid), PrimaryKey
    * @param code    Database column code SqlType(varchar)
    * @param userId  Database column user_id SqlType(uuid)
    * @param validTo Database column valid_to SqlType(OffsetDateTime) */
  case class PasswordResetCodesRow(
    id: java.util.UUID,
    code: String,
    userId: java.util.UUID,
    validTo: OffsetDateTime
  )

  /** GetResult implicit for fetching PasswordResetCodesRow objects using plain SQL queries */
  implicit def GetResultPasswordResetCodesRow(
    implicit e0: GR[java.util.UUID],
    e1: GR[String],
    e2: GR[OffsetDateTime]): GR[PasswordResetCodesRow] = GR { prs =>
      import prs._
      PasswordResetCodesRow.tupled((<<[java.util.UUID], <<[String], <<[java.util.UUID], <<[OffsetDateTime]))
  }

  /** Table description of table PASSWORD_RESET_CODES. Objects of this class serve as prototypes for rows in queries. */
  class PasswordResetCodes(_tableTag: Tag) extends profile.api.Table[PasswordResetCodesRow](
    _tableTag, "PASSWORD_RESET_CODES"
  ) {
    def * = (id, code, userId, validTo) <> (PasswordResetCodesRow.tupled, PasswordResetCodesRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(code), Rep.Some(userId), Rep.Some(validTo)).shaped.<>(
      { r => import r._; _1.map(_ => PasswordResetCodesRow.tupled((_1.get, _2.get, _3.get, _4.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(uuid), PrimaryKey */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id", O.PrimaryKey)
    /** Database column code SqlType(varchar) */
    val code: Rep[String] = column[String]("code")
    /** Database column user_id SqlType(uuid) */
    val userId: Rep[java.util.UUID] = column[java.util.UUID]("user_id")
    /** Database column valid_to SqlType(OffsetDateTime) */
    val validTo: Rep[OffsetDateTime] = column[OffsetDateTime]("valid_to")

    /** Foreign key referencing Users (database name PASSWORD_RESET_CODES_user_id_fkey) */
    lazy val usersFk = foreignKey("PASSWORD_RESET_CODES_user_id_fkey", userId, users)(
      r => r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade
    )
  }

  /** Collection-like TableQuery object for table PasswordResetCodes */
  lazy val passwordResetCodes = new TableQuery(tag => new PasswordResetCodes(tag))

  import com.jc.api.model.Pilot

  /** GetResult implicit for fetching PilotsRow objects using plain SQL queries */
  implicit def GetResultPilotsRow(
    implicit e0: GR[Int],
    e1: GR[java.util.UUID],
    e2: GR[String]): GR[Pilot] = GR { prs =>
      import prs._
      Pilot.tupled((<<[Int], <<[java.util.UUID], <<[String]))
  }

  /** Table description of table PILOTS. Objects of this class serve as prototypes for rows in queries. */
  class Pilots(_tableTag: Tag) extends profile.api.Table[Pilot](_tableTag, "PILOTS") {
    def * = (id, userId, licenseNum) <> (Pilot.tupled, Pilot.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(licenseNum)).shaped.<>(
      { r => import r._; _1.map(_ => Pilot.tupled((_1.get, _2.get, _3.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(uuid) */
    val userId: Rep[java.util.UUID] = column[java.util.UUID]("user_id")
    /** Database column license_num SqlType(varchar) */
    val licenseNum: Rep[String] = column[String]("license_num")

    /** Foreign key referencing Users (database name PILOTS_user_id_fkey) */
    lazy val usersFk = foreignKey("PILOTS_user_id_fkey", userId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table Pilots */
  lazy val pilots = new TableQuery(tag => new Pilots(tag))

  /** Entity class storing rows of table PlaneProviderMappings
    *
    * @param providerId Database column provider_id SqlType(int4)
    * @param planeId    Database column plane_id SqlType(int4) */
  case class PlaneProviderMappingsRow(providerId: Int, planeId: Int)

  /** GetResult implicit for fetching PlaneProviderMappingsRow objects using plain SQL queries */
  implicit def GetResultPlaneProviderMappingsRow(implicit e0: GR[Int]): GR[PlaneProviderMappingsRow] = GR {
    prs =>
      import prs._
      PlaneProviderMappingsRow.tupled((<<[Int], <<[Int]))
  }

  /** Table description of table PLANE_PROVIDER_MAPPINGS. Objects of this class serve as prototypes for rows in queries. */
  class PlaneProviderMappings(_tableTag: Tag) extends profile.api.Table[PlaneProviderMappingsRow](
    _tableTag, "PLANE_PROVIDER_MAPPINGS"
  ) {
    def * = (providerId, planeId) <> (PlaneProviderMappingsRow.tupled, PlaneProviderMappingsRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(providerId), Rep.Some(planeId)).shaped.<>(
      { r => import r._; _1.map(_ => PlaneProviderMappingsRow.tupled((_1.get, _2.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column provider_id SqlType(int4) */
    val providerId: Rep[Int] = column[Int]("provider_id")
    /** Database column plane_id SqlType(int4) */
    val planeId: Rep[Int] = column[Int]("plane_id")

    /** Foreign key referencing Planes (database name PLANE_PROVIDER_MAPPINGS_plane_id_fkey) */
    lazy val planesFk = foreignKey("PLANE_PROVIDER_MAPPINGS_plane_id_fkey", planeId, planes)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
    /** Foreign key referencing PlaneProviders (database name PLANE_PROVIDER_MAPPINGS_provider_id_fkey) */
    lazy val planeProvidersFk = foreignKey("PLANE_PROVIDER_MAPPINGS_provider_id_fkey", providerId, planeProviders)(
      r => r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade
    )
  }

  /** Collection-like TableQuery object for table PlaneProviderMappings */
  lazy val planeProviderMappings = new TableQuery(tag => new PlaneProviderMappings(tag))

  /** Entity class storing rows of table PlaneProviders
    *
    * @param id          Database column id SqlType(serial), AutoInc, PrimaryKey
    * @param userId      Database column user_id SqlType(uuid)
    * @param lincenceNum Database column lincence_num SqlType(varchar) */
  case class PlaneProvidersRow(
    id: Int,
    userId: java.util.UUID,
    lincenceNum: String
  )

  /** GetResult implicit for fetching PlaneProvidersRow objects using plain SQL queries */
  implicit def GetResultPlaneProvidersRow(
    implicit e0: GR[Int],
    e1: GR[java.util.UUID],
    e2: GR[String]
  ): GR[PlaneProvidersRow] = GR { prs =>
      import prs._
      PlaneProvidersRow.tupled((<<[Int], <<[java.util.UUID], <<[String]))
  }

  /** Table description of table PLANE_PROVIDERS. Objects of this class serve as prototypes for rows in queries. */
  class PlaneProviders(_tableTag: Tag) extends profile.api.Table[PlaneProvidersRow](_tableTag, "PLANE_PROVIDERS") {
    def * = (id, userId, lincenceNum) <> (PlaneProvidersRow.tupled, PlaneProvidersRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(lincenceNum)).shaped.<>(
      { r => import r._; _1.map(_ => PlaneProvidersRow.tupled((_1.get, _2.get, _3.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(uuid) */
    val userId: Rep[java.util.UUID] = column[java.util.UUID]("user_id")
    /** Database column lincence_num SqlType(varchar) */
    val lincenceNum: Rep[String] = column[String]("lincence_num")

    /** Foreign key referencing Users (database name PLANE_PROVIDERS_user_id_fkey) */
    lazy val usersFk = foreignKey("PLANE_PROVIDERS_user_id_fkey", userId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table PlaneProviders */
  lazy val planeProviders = new TableQuery(tag => new PlaneProviders(tag))

  import com.jc.api.model.Plane

  /** GetResult implicit for fetching PlanesRow objects using plain SQL queries */
  implicit def GetResultPlanesRow(
    implicit e0: GR[Int],
    e1: GR[String],
    e2: GR[java.util.UUID]
  ): GR[Plane] = GR { prs =>
      import prs._
      Plane.tupled(
        (<<[Int], <<[String], <<[String], <<[String], <<[String], <<[java.util.UUID], <<[Int], <<[Int], <<[Int])
      )
  }

  /** Table description of table PLANES. Objects of this class serve as prototypes for rows in queries. */
  class Planes(_tableTag: Tag) extends profile.api.Table[Plane](_tableTag, "PLANES") {
    def * = (id, nNo, manufacturerName, serialNo, model, ownerId, pilotSeats, minPilot, customerSeats) <> (Plane.tupled, Plane.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(nNo), Rep.Some(manufacturerName), Rep.Some(serialNo), Rep.Some(model), Rep.Some(
      ownerId
    ), Rep.Some(pilotSeats), Rep.Some(minPilot), Rep.Some(customerSeats)).shaped.<>(
      { r => import r._; _1.map(
        _ => Plane.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get))
      )
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column n_no SqlType(varchar) */
    val nNo: Rep[String] = column[String]("n_no")
    /** Database column manufacturer_name SqlType(varchar) */
    val manufacturerName: Rep[String] = column[String]("manufacturer_name")
    /** Database column serial_no SqlType(varchar) */
    val serialNo: Rep[String] = column[String]("serial_no")
    /** Database column model SqlType(varchar) */
    val model: Rep[String] = column[String]("model")
    /** Database column owner_id SqlType(uuid) */
    val ownerId: Rep[java.util.UUID] = column[java.util.UUID]("owner_id")
    /** Database column pilot_seats SqlType(int4) */
    val pilotSeats: Rep[Int] = column[Int]("pilot_seats")
    /** Database column min_pilot SqlType(int4) */
    val minPilot: Rep[Int] = column[Int]("min_pilot")
    /** Database column customer_seats SqlType(int4) */
    val customerSeats: Rep[Int] = column[Int]("customer_seats")

    /** Foreign key referencing Users (database name PLANES_owner_id_fkey) */
    lazy val usersFk = foreignKey("PLANES_owner_id_fkey", ownerId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table Planes */
  lazy val planes = new TableQuery(tag => new Planes(tag))

  /** Entity class storing rows of table ProvideOrders
    *
    * @param providerAskId Database column provider_ask_id SqlType(int8)
    * @param consumerBidId Database column consumer_bid_id SqlType(int8)
    * @param confirmed     Database column confirmed SqlType(bool), Default(Some(false))
    * @param createdOn     Database column created_on SqlType(OffsetDateTime)
    * @param modifiedOn    Database column modified_on SqlType(OffsetDateTime) */
  case class ProvideOrdersRow(
    providerAskId: Long,
    consumerBidId: Long,
    confirmed: Option[Boolean] = Some(false),
    createdOn: OffsetDateTime,
    modifiedOn: OffsetDateTime
  )

  /** GetResult implicit for fetching ProvideOrdersRow objects using plain SQL queries */
  implicit def GetResultProvideOrdersRow(
    implicit e0: GR[Long],
    e1: GR[Option[Boolean]],
    e2: GR[OffsetDateTime]
  ): GR[ProvideOrdersRow] = GR { prs =>
      import prs._
      ProvideOrdersRow.tupled((<<[Long], <<[Long], <<?[Boolean], <<[OffsetDateTime], <<[OffsetDateTime]))
  }

  /** Table description of table PROVIDE_ORDERS. Objects of this class serve as prototypes for rows in queries. */
  class ProvideOrders(_tableTag: Tag) extends profile.api.Table[ProvideOrdersRow](_tableTag, "PROVIDE_ORDERS") {
    def * = (providerAskId, consumerBidId, confirmed, createdOn, modifiedOn) <> (ProvideOrdersRow.tupled, ProvideOrdersRow.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(providerAskId), Rep.Some(consumerBidId), confirmed, Rep.Some(createdOn), Rep.Some(modifiedOn
    )).shaped.<>(
      { r => import r._; _1.map(_ => ProvideOrdersRow.tupled((_1.get, _2.get, _3, _4.get, _5.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column provider_ask_id SqlType(int8) */
    val providerAskId: Rep[Long] = column[Long]("provider_ask_id")
    /** Database column consumer_bid_id SqlType(int8) */
    val consumerBidId: Rep[Long] = column[Long]("consumer_bid_id")
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val confirmed: Rep[Option[Boolean]] = column[Option[Boolean]]("confirmed", O.Default(Some(false)))
    /** Database column created_on SqlType(OffsetDateTime) */
    val createdOn: Rep[OffsetDateTime] = column[OffsetDateTime]("created_on")
    /** Database column modified_on SqlType(OffsetDateTime) */
    val modifiedOn: Rep[OffsetDateTime] = column[OffsetDateTime]("modified_on")

    /** Foreign key referencing ConsumerBids (database name PROVIDE_ORDERS_consumer_bid_id_fkey) */
    lazy val consumerBidsFk = foreignKey("PROVIDE_ORDERS_consumer_bid_id_fkey", consumerBidId, consumerBids)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
    /** Foreign key referencing ProviderAsks (database name PROVIDE_ORDERS_provider_ask_id_fkey) */
    lazy val providerAsksFk = foreignKey("PROVIDE_ORDERS_provider_ask_id_fkey", providerAskId, providerAsks)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table ProvideOrders */
  lazy val provideOrders = new TableQuery(tag => new ProvideOrders(tag))

  import com.jc.api.model.ProviderAsk

  /** GetResult implicit for fetching ProviderAsksRow objects using plain SQL queries */
  implicit def GetResultProviderAsksRow(
    implicit e0: GR[Long],
    e1: GR[Option[Long]],
    e2: GR[java.util.UUID],
    e3: GR[Int], e4: GR[Double],
    e5: GR[Option[Boolean]],
    e6: GR[OffsetDateTime]
  ): GR[ProviderAsk] = GR { prs =>
      import prs._
      ProviderAsk.tupled(
        (<<[Long], <<?[Long], <<[java.util.UUID], <<[Int], <<[Double], <<?[Boolean], <<[OffsetDateTime], <<[OffsetDateTime])
      )
  }

  /** Table description of table PROVIDER_ASKS. Objects of this class serve as prototypes for rows in queries. */
  class ProviderAsks(_tableTag: Tag) extends profile.api.Table[ProviderAsk](_tableTag, "PROVIDER_ASKS") {
    def * = (id, planId, providerId, seats, price, active, createdOn, modifiedOn) <> (ProviderAsk.tupled, ProviderAsk.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), planId, Rep.Some(providerId), Rep.Some(seats), Rep.Some(price), active, Rep.Some(createdOn
    ), Rep.Some(modifiedOn)).shaped.<>(
      { r => import r._; _1.map(_ => ProviderAsk.tupled((_1.get, _2, _3.get, _4.get, _5.get, _6, _7.get, _8.get))) },
      (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column plan_id SqlType(int8), Default(None) */
    val planId: Rep[Option[Long]] = column[Option[Long]]("plan_id", O.Default(None))
    /** Database column provider_id SqlType(uuid) */
    val providerId: Rep[java.util.UUID] = column[java.util.UUID]("provider_id")
    /** Database column seats SqlType(int4) */
    val seats: Rep[Int] = column[Int]("seats")
    /** Database column price SqlType(float8) */
    val price: Rep[Double] = column[Double]("price")
    /** Database column active SqlType(bool), Default(Some(true)) */
    val active: Rep[Option[Boolean]] = column[Option[Boolean]]("active", O.Default(Some(true)))
    /** Database column created_on SqlType(OffsetDateTime) */
    val createdOn: Rep[OffsetDateTime] = column[OffsetDateTime]("created_on")
    /** Database column modified_on SqlType(OffsetDateTime) */
    val modifiedOn: Rep[OffsetDateTime] = column[OffsetDateTime]("modified_on")

    /** Foreign key referencing FlightPlans (database name PROVIDER_ASKS_plan_id_fkey) */
    lazy val flightPlansFk = foreignKey("PROVIDER_ASKS_plan_id_fkey", planId, flightPlans)(
      r => Rep.Some(r.id), onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
    /** Foreign key referencing Users (database name PROVIDER_ASKS_provider_id_fkey) */
    lazy val usersFk = foreignKey("PROVIDER_ASKS_provider_id_fkey", providerId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table ProviderAsks */
  lazy val providerAsks = new TableQuery(tag => new ProviderAsks(tag))

  import com.jc.api.model.ProviderBid

  /** GetResult implicit for fetching ProviderBidsRow objects using plain SQL queries */
  implicit def GetResultProviderBidsRow(
    implicit e0: GR[Long],
    e1: GR[java.util.UUID],
    e2: GR[Option[Boolean]],
    e3: GR[OffsetDateTime]
  ): GR[ProviderBid] = GR { prs =>
      import prs._
      ProviderBid.tupled(
        (<<[Long], <<[Long], <<[java.util.UUID], <<[Double], <<?[Boolean], <<[Boolean], <<[Boolean], <<[Boolean], <<[OffsetDateTime], <<[OffsetDateTime])
      )
  }

  /** Table description of table PROVIDER_BIDS. Objects of this class serve as prototypes for rows in queries. */
  class ProviderBids(_tableTag: Tag) extends profile.api.Table[ProviderBid](_tableTag, "PROVIDER_BIDS") {
    def * = (id, consumerAskId, bidderId, price, active, confirmed, charged, refunded, createdOn, modifiedOn) <> (ProviderBid.tupled, ProviderBid.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(consumerAskId), Rep.Some(bidderId
    ), price, active, confirmed, charged, refunded, Rep.Some(createdOn), Rep.Some(modifiedOn)).shaped.<>(
      { r => import r._; _1.map(_ => ProviderBid.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7, _8, _9.get, _10.get))
      )
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported.")
    )

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column consumer_ask_id SqlType(int8) */
    val consumerAskId: Rep[Long] = column[Long]("consumer_ask_id")
    /** Database column bidder_id SqlType(uuid) */
    val bidderId: Rep[java.util.UUID] = column[java.util.UUID]("bidder_id")
    /** Database column price SqlType(float8) */
    val price: Rep[Double] = column[Double]("price")
    /** Database column active SqlType(bool), Default(Some(true)) */
    val active: Rep[Option[Boolean]] = column[Option[Boolean]]("active", O.Default(Some(true)))
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val confirmed: Rep[Boolean] = column[Boolean]("confirmed", O.Default(false))
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val charged: Rep[Boolean] = column[Boolean]("charged", O.Default(false))
    /** Database column confirmed SqlType(bool), Default(Some(false)) */
    val refunded: Rep[Boolean] = column[Boolean]("refunded", O.Default(false))
    /** Database column created_on SqlType(OffsetDateTime) */
    val createdOn: Rep[OffsetDateTime] = column[OffsetDateTime]("created_on")
    /** Database column modified_on SqlType(OffsetDateTime) */
    val modifiedOn: Rep[OffsetDateTime] = column[OffsetDateTime]("modified_on")

    /** Foreign key referencing ConsumerAsks (database name PROVIDER_BIDS_consumer_ask_id_fkey) */
    lazy val consumerAsksFk = foreignKey("PROVIDER_BIDS_consumer_ask_id_fkey", consumerAskId, consumerAsks)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
    /** Foreign key referencing Users (database name PROVIDER_BIDS_bidder_id_fkey) */
    lazy val usersFk = foreignKey("PROVIDER_BIDS_bidder_id_fkey", bidderId, users)(
      r => r.id, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction
    )
  }

  /** Collection-like TableQuery object for table ProviderBids */
  lazy val providerBids = new TableQuery(tag => new ProviderBids(tag))


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
