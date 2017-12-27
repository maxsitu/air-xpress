package com.jc.api.model

import java.time.OffsetDateTime

import com.jc.api.endpoint.ask.AskId
import com.jc.api.endpoint.user.UserId

/** Entity class storing rows of table ConsumerAsks
  *
  *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
  *  @param planId Database column plan_id SqlType(int8), Default(None)
  *  @param consumerId Database column consumer_id SqlType(uuid)
  *  @param passengers Database column passengers SqlType(int8)
  *  @param active Database column active SqlType(bool), Default(Some(true))
  *  @param askedOn Database column asked_on SqlType(OffsetDateTime)
  *  @param modifiedOn Database column modified_on SqlType(OffsetDateTime) */
case class ConsumerAsk(id: AskId, planId: Option[Long] = None, consumerId: UserId, passengers: Int, active: Option[Boolean] = Some(true), askedOn: OffsetDateTime, modifiedOn: OffsetDateTime)

/** Entity class storing rows of table ProviderAsks
  *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
  *  @param planId Database column plan_id SqlType(int8), Default(None)
  *  @param providerId Database column provider_id SqlType(uuid)
  *  @param seats Database column seats SqlType(int4)
  *  @param price Database column price SqlType(float8)
  *  @param active Database column active SqlType(bool), Default(Some(true))
  *  @param createdOn Database column created_on SqlType(OffsetDateTime)
  *  @param modifiedOn Database column modified_on SqlType(OffsetDateTime) */
case class ProviderAsk(id: AskId, planId: Option[Long] = None, providerId: UserId, seats: Int, price: Double, active: Option[Boolean] = Some(true), createdOn: OffsetDateTime, modifiedOn: OffsetDateTime)