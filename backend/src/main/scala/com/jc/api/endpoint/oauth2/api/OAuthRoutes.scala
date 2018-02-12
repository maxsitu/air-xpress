package com.jc.api.endpoint.oauth2.api

import akka.http.scaladsl.model.{StatusCodes, Uri}
import com.jc.api.common.api.RoutesSupport
import com.jc.api.endpoint.user.api.SessionSupport
import com.typesafe.scalalogging.StrictLogging
import akka.http.scaladsl.server.Directives._
import com.airxpress.api.oauth.GoogleOauth2Helper

trait OAuthRoutes extends RoutesSupport with StrictLogging with SessionSupport{

  val flow = GoogleOauth2Helper.prepareAuthorizationCodeFlow()

  val oauthRoutes = pathPrefix("googleOauth2") {
    path("callback") {
      get {
        parameter('error.?, 'code.?) { (error, code) =>
          val userInfo = GoogleOauth2Helper.getUserInfo(GoogleOauth2Helper.prepareCredential(flow, code.get))
          complete(s"error: $error\ncode: $code\nuserInfo: $userInfo")
        }
      }
    } ~
    pathEnd {
      get {
        redirect(Uri(GoogleOauth2Helper.prepareRedirectUrl(flow)), StatusCodes.PermanentRedirect)
      }
    }
  }
}
