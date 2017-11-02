package com.jc.api.model

import com.jc.api.endpoint.pilot.PilotId
import com.jc.api.endpoint.user.UserId

/** Entity class storing rows of table Pilots
  *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
  *  @param userId Database column user_id SqlType(uuid)
  *  @param licenseNum Database column license_num SqlType(varchar) */
case class Pilot (id: PilotId, userId: UserId, licenseNum: String)
