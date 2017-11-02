package com.jc.api.model

import java.time.OffsetDateTime
import java.util.UUID

/** Entity class storing rows of table RememberMeTokens
  *  @param id Database column id SqlType(uuid), PrimaryKey
  *  @param selector Database column selector SqlType(varchar)
  *  @param tokenHash Database column token_hash SqlType(varchar)
  *  @param userId Database column user_id SqlType(uuid)
  *  @param validTo Database column valid_to SqlType(OffsetDateTime) */
case class RememberMeToken(id: UUID, selector: String, tokenHash: String, userId: UUID, validTo: OffsetDateTime)
