package com.jc.api.endpoint.location.application

import com.jc.api.endpoint.location.LocationId
import com.jc.api.endpoint.location.application.LocationModifyResult.{InvalidData, LocationNotExists}
import com.jc.api.model.Location

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by walle on 7/14/17.
  */
class LocationService (
  locationDao: LocationDao
)(implicit ec: ExecutionContext){

  def addLocation(code: String, name: String, geoLat: Double, geoLon: Double): Future[Either[LocationModifyResult, LocationId]] = {
    def checkLocationExistence(): Future[Either[LocationModifyResult.LocationExists, Unit]] = {
      val existingLocationFuture = locationDao.findByCode(code)
      existingLocationFuture map  {
        case Some(_) => Left(LocationModifyResult.LocationExists(code))
        case None => Right((): Unit)
      }
    }

    val location = Location(0, code, name, geoLat, geoLon);
    LocationTransformValidator.validateLocation(location) match {
        case LocationModifyResult.Success => checkLocationExistence().flatMap {
          case Left(result) => Future.successful(Left(result))
          case Right(_) => locationDao.add(location).map(Right(_))
        }
        case invalid => Future.successful(Left(invalid))
      }

  }

  def updateLocation(id: LocationId, code: String, name: String, geoLat: Double, geoLon: Double): Future[Either[LocationModifyResult, LocationId]] = {
    def checkLocationExistence(): Future[Either[LocationNotExists, LocationId]] = {
      val existingLocationFuture = locationDao.findById(id)
      existingLocationFuture map  {
        case None => Left(LocationNotExists(id))
        case Some(_) => Right(id)
      }
    }

    val location = Location(id, code, name, geoLat, geoLon)
    LocationTransformValidator.validateLocation(location) match {
      case LocationModifyResult.Success => checkLocationExistence().flatMap {
        case Right(_) => locationDao.update(location).map(Right(_))
        case a @ Left(_) => Future.successful(a)
      }
      case invalid => Future.successful(Left(invalid))
    }
  }


  def findByLocationId(id: LocationId): Future[Option[Location]] =
    locationDao.findById(id)

  def findByLocationCode(code: String): Future[Option[Location]] =
    locationDao.findByCode(code)

  def findByLocationCodePrefix(codePrefix: String): Future[Seq[Location]] =
    locationDao.findByCodePrefix(codePrefix)

  def findByLocationNamePrefix(namePrefix: String): Future[Seq[Location]] =
    locationDao.findByNamePrefix(namePrefix)

  def findAll(): Future[Seq[Location]] =
    locationDao.findAll()
}

sealed trait LocationModifyResult

object LocationModifyResult {
  object ERROR_MSG {
    val LAT_NOT_DOUBLE  = "Geo latitude is not a float!"
    val LON_NOT_DOUBLE  = "Geo longitude is not a float!"
    val LAT_INVALID     = "Geo latitude is not valid!"
    val LON_INVALID     = "Geo Longitude is not valid!"
  }

  case class InvalidData(geoLat: Option[String] = None, geoLon: Option[String] = None) extends LocationModifyResult{
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

  case class LocationExists(code: String) extends LocationModifyResult

  case class LocationNotExists(id: LocationId) extends LocationModifyResult

  case object Success extends LocationModifyResult
}

object LocationTransformValidator {

  def toLocation(code: String, name: String, geoLat: Double, geoLon: Double): Either[LocationModifyResult, Location] = Right(Location(0, code, name, geoLat, geoLon))

  def validateLocation(location: Location):LocationModifyResult = {
    def isValidGeo(num: Double): Boolean = (num <=180 && num >= -180)

    if (Seq(location.geoLat, location.geoLon).forall(isValidGeo)) {
      LocationModifyResult.Success
    }
    else {
      var result = LocationModifyResult.InvalidData()
      if (!isValidGeo(location.geoLat))
        result = result.withInvalidGeoLat
      if (!isValidGeo(location.geoLon))
        result = result.withInvalidGeoLon

      result
    }
  }
}