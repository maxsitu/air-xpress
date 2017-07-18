package com.jc.api.location.application

import com.jc.api.location.LocationId
import com.jc.api.location.application.LocationAddResult.InvalidData
import com.jc.api.location.domain.Location

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by walle on 7/14/17.
  */
class LocationService (
  locationDao: LocationDao
)(implicit ec: ExecutionContext){

  def addLocation(code: String, name: String, geoLat: String, geoLon: String): Future[Either[LocationAddResult, LocationId]] = {
    def checkLocationExistence(): Future[Either[LocationAddResult.LocationExists, Unit]] = {
      val existingLocationFuture = locationDao.findByCode(code)
      existingLocationFuture map  {
        case Some(_) => Left(LocationAddResult.LocationExists(code))
        case None => Right((): Unit)
      }
    }

    LocationTransformValidator.toLocation(code, name, geoLat, geoLon).fold(
      result =>
        Future.successful(Left(result)),

      location =>
        LocationTransformValidator.validateLocation(location) match {
          case LocationAddResult.Success => checkLocationExistence().flatMap {
            case Left(result) => Future.successful(Left(result))
            case Right(_) => locationDao.add(location).map(Right(_))
          }
          case invalid => Future.successful(Left(invalid))
        }
    )
  }

  def findByLocationId(id: LocationId): Future[Option[Location]] =
    locationDao.findById(id)

  def findByLocationCode(code: String): Future[Option[Location]] =
    locationDao.findByCode(code)

  def findAll(): Future[Seq[Location]] =
    locationDao.findAll()
}

sealed trait LocationAddResult

object LocationAddResult {
  object ERROR_MSG {
    val LAT_NOT_DOUBLE  = "Geo latitude is not a float!"
    val LON_NOT_DOUBLE  = "Geo longitude is not a float!"
    val LAT_INVALID     = "Geo latitude is not valid!"
    val LON_INVALID     = "Geo Longitude is not valid!"
  }

  case class InvalidData(geoLat: Option[String] = None, geoLon: Option[String] = None) extends LocationAddResult{
    def withGeoLat(geoLat: Try[Double]): InvalidData = geoLat match {
      case scala.util.Success(_) => InvalidData(geoLat = None, geoLon = geoLon)
      case Failure(_) => InvalidData(geoLat = Some(ERROR_MSG.LAT_NOT_DOUBLE), geoLon = geoLon)
    }

    def withGeoLon(geoLon: Try[Double]): InvalidData = geoLon match {
      case scala.util.Success(_) => InvalidData(geoLat = geoLat, geoLon = None)
      case Failure(_) => InvalidData(geoLat = geoLat, geoLon = Some(ERROR_MSG.LON_NOT_DOUBLE))
    }

    def withInvalidGeoLat: InvalidData = InvalidData(geoLat = Some(ERROR_MSG.LAT_INVALID), geoLon = geoLon)

    def withInvalidGeoLon: InvalidData = InvalidData(geoLat = geoLat, geoLon = Some(ERROR_MSG.LON_INVALID))
  }

  case class LocationExists(code: String) extends LocationAddResult

  case object Success extends LocationAddResult
}

object LocationTransformValidator {

  def toLocation(code: String, name: String, geoLat: String, geoLon: String): Either[LocationAddResult, Location] = {

    val (lat, lon) = (Try (geoLat.toDouble), Try (geoLon.toDouble))

    if (Seq(lat, lon).forall(_.isSuccess))
      Right(Location(0, code, name, lat.get, lon.get))
    else
      Left(LocationAddResult.InvalidData().withGeoLat(lat).withGeoLon(lon))
  }

  def validateLocation(location: Location):LocationAddResult = {
    def isValidGeo(num: Double): Boolean = (num <=180 && num >= -180)

    if (Seq(location.geoLat, location.geoLon).forall(isValidGeo)) {
      LocationAddResult.Success
    }
    else {
      var result = LocationAddResult.InvalidData()
      if (!isValidGeo(location.geoLat))
        result = result.withInvalidGeoLat
      if (!isValidGeo(location.geoLon))
        result = result.withInvalidGeoLon

      result
    }
  }
}