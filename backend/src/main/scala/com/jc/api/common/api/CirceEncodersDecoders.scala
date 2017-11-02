package com.jc.api.common.api

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes.ClientError
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
      Right(OffsetDateTime.from(dateTimeFormat.parse(c.top.get.noSpaces)))
  }

  implicit object BooleanOptionDecoder extends Decoder[Option[Boolean]] {
    override def apply(c: HCursor): Result[Option[Boolean]] =
      Right(Try(c.top.get.noSpaces.toBoolean).map(Some(_)).getOrElse(Some(false)))
  }
}
