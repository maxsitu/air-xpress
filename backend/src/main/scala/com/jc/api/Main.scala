package com.jc.api

import java.util.Locale

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.DateTime
import akka.http.scaladsl.model.headers.HttpCookie
import akka.stream.ActorMaterializer
import com.jc.api.endpoint.user.application.Session
import com.softwaremill.session.{SessionConfig, SessionManager, SessionUtil}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future
import scala.util.{Failure, Success}

class Main() extends StrictLogging {
  def start(): (Future[ServerBinding], DependencyWiring) = {
    Locale.setDefault(Locale.US) // set default locale to prevent from sending cookie expiration date in polish format

    implicit val _system       = ActorSystem("main")
    implicit val _materializer = ActorMaterializer()
    import _system.dispatcher

    val modules = new DependencyWiring with Routes {

      lazy val sessionConfig = SessionConfig.fromConfig(config.rootConfig).copy(sessionEncryptData = true)

      implicit lazy val ec                                      = _system.dispatchers.lookup("akka-http-routes-dispatcher")
      implicit lazy val sessionManager: SessionManager[Session] = new SessionManager[Session](sessionConfig) {
        def createToken(): String = SessionUtil.randomString(64)
        def createCookie() = HttpCookie(
          name = config.csrfCookieConfig.name,
          value = createToken(),
          expires = Some(DateTime.now + config.refreshTokenMaxAgeSeconds * 1000L),
          domain = config.csrfCookieConfig.domain,
          path = config.csrfCookieConfig.path,
          secure = config.csrfCookieConfig.secure,
          httpOnly = config.csrfCookieConfig.httpOnly
        )
      }
      implicit lazy val materializer                            = _materializer
      lazy val system                                           = _system
    }

    logger.info("Server secret: " + modules.sessionConfig.serverSecret.take(3) + "...")

    modules.sqlDatabase.updateSchema()

    (Http().bindAndHandle(modules.routes, modules.config.serverHost, modules.config.serverPort), modules)
  }
}

object Main extends App with StrictLogging {
  val (startFuture, bl) = new Main().start()

  val host = bl.config.serverHost
  val port = bl.config.serverPort

  val system = bl.system
  import system.dispatcher

  startFuture.onComplete {
    case Success(b) =>
      logger.info(s"Server started on $host:$port")
      sys.addShutdownHook {
        b.unbind()
        bl.system.terminate()
        logger.info("Server stopped")
      }
    case Failure(e) =>
      logger.error(s"Cannot start server on $host:$port", e)
      sys.addShutdownHook {
        bl.system.terminate()
        logger.info("Server stopped")
      }
  }
}
