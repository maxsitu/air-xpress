package com.jc.api.model

import java.time.OffsetDateTime

import com.jc.api.endpoint.ask.AskId
import com.jc.api.endpoint.bid.BidId
import com.jc.api.endpoint.user.UserId

/** Entity class storing rows of table ConsumerBids
  *
  *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
  *  @param providerAskId Database column provider_ask_id SqlType(int8)
  *  @param bidderId Database column bidder_id SqlType(uuid)
  *  @param passengers Database column passengers SqlType(int8)
  *  @param active Database column active SqlType(bool), Default(Some(true))
  *  @param confirmed Database column confirmed SqlType(bool), Default(false)
  *  @param charged Database column charged SqlType(bool), Default(false)
  *  @param refunded Database column refunded SqlType(bool), Default(false)
  *  @param createdOn Database column created_on SqlType(OffsetDateTime)
  *  @param modifiedOn Database column modified_on SqlType(OffsetDateTime) */
case class ConsumerBid(
  id: BidId,
  providerAskId: AskId,
  bidderId: UserId,
  passengers: Int,
  active: Option[Boolean] = Some(true),
  confirmed: Boolean = false,
  charged: Boolean = false,
  refunded: Boolean = false,
  createdOn: OffsetDateTime,
  modifiedOn: OffsetDateTime
)

/** Entity class storing rows of table ProviderBids
  *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
  *  @param consumerAskId Database column consumer_ask_id SqlType(int8)
  *  @param bidderId Database column bidder_id SqlType(uuid)
  *  @param price Database column price SqlType(float8)
  *  @param active Database column active SqlType(bool), Default(Some(true))
  *  @param confirmed Database column confirmed SqlType(bool), Default(Some(false))
  *  @param charged Database column charged SqlType(bool), Default(false)
  *  @param refunded Database column refunded SqlType(bool), Default(false)
  *  @param createdOn Database column created_on SqlType(OffsetDateTime)
  *  @param modifiedOn Database column modified_on SqlType(OffsetDateTime) */
case class ProviderBid(
  id: BidId,
  consumerAskId: AskId,
  bidderId: UserId,
  price: Double,
  active: Option[Boolean] = Some(true),
  confirmed: Boolean = false,
  charged: Boolean = false,
  refunded: Boolean = false,
  createdOn: OffsetDateTime,
  modifiedOn: OffsetDateTime
)