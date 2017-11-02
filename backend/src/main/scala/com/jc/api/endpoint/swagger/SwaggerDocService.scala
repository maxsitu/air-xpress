package com.jc.api.endpoint.swagger

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.swagger.akka._
import com.github.swagger.akka.model.Info
import com.jc.api.endpoint.version.VersionRoutes
import com.jc.api.endpoint.version.BuildInfo._

import scala.reflect.runtime.{universe => ua}

class SwaggerDocService(address: String, port: Int, system: ActorSystem) extends SwaggerHttpService with HasActorSystem {
  override implicit val actorSystem: ActorSystem = system
  override implicit val materializer: ActorMaterializer = ActorMaterializer()
  override val apiTypes = Seq( // add here routes in order to add to swagger
    ua.typeOf[VersionRoutes]
  )
  override val host = address + ":" + port
  override val info = Info(version = buildDate, title = "JC Account")
  override val apiDocsPath = "api-docs"
}
