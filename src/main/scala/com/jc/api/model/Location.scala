package com.jc.api.model

import com.jc.api.endpoint.location.LocationId

/**
  * Created by walle on 7/13/17.
  */
case class Location (
   id     :   LocationId,
   code   : String,
   name   : String,
   geoLat : Double,
   geoLon : Double
)
