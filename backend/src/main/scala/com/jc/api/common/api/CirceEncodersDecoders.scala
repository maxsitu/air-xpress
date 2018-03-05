package com.jc.api.common.api

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes.ClientError
import com.jc.api.endpoint.ask.api.BasicProviderAskData
import com.jc.api.endpoint.flight.api.{BasicFlightPlan, BasicFlightStep}
import com.jc.api.endpoint.location.LocationId
import io.circe.Decoder.Result
import io.circe._
import io.circe.syntax._

import scala.util.Try

trait CirceEncodersDecoders {

  val dateTimeFormat = DateTimeFormatter.ISO_DATE_TIME

  implicit object DateTimeEncoder extends Encoder[OffsetDateTime] {
    override def apply(dt: OffsetDateTime): Json = dateTimeFormat.format(dt).asJson
  }

  implicit object UuidEncoder extends Encoder[UUID] {
    override def apply(u: UUID): Json = u.toString.asJson
  }

  implicit object ClientErrorEncoder extends Encoder[ClientError] {
    override def apply(a: ClientError): Json = a.defaultMessage.asJson
  }

  implicit object DateTimeDecoder extends Decoder[OffsetDateTime] {
    override def apply(c: HCursor): Result[OffsetDateTime] =
      Right(OffsetDateTime.from(dateTimeFormat.parse(c.as[String].getOrElse(""))))
  }

  implicit object BasicFlightStepDecoder extends Decoder[BasicFlightStep] {
    override def apply(c: HCursor): Result[BasicFlightStep] =
    for {
      frmLocId <- c.get[LocationId]("fromLocationId")
      toLocId  <- c.get[LocationId]("toLocationId")
      fromTime <- c.get[OffsetDateTime]("fromTime")
      toTime   <- c.get[OffsetDateTime]("toTime")
    } yield BasicFlightStep(frmLocId, toLocId, fromTime, toTime)
  }

  implicit object BasicFlightPlanDecoder extends Decoder[BasicFlightPlan] {
    override def apply(c: HCursor): Result[BasicFlightPlan] =
    for {
      passengerNum <- c.get[Int]("passengerNum")
      flightSteps  <- c.get[List[BasicFlightStep]]("flightSteps")
    } yield BasicFlightPlan(passengerNum, flightSteps)
  }

  implicit object BasicProviderAskDataDecoder extends Decoder[BasicProviderAskData] {
    override def apply(c: HCursor): Result[BasicProviderAskData] =
      for {
        seats <- c.get[Int]("seats")
        price    <- c.get[Double]("price")
        flightPlan  <- c.get[BasicFlightPlan]("plan")
      } yield BasicProviderAskData(seats, price, flightPlan)
  }
}
