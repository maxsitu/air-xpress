package com.jc.api.email.application

import com.jc.api.email.domain.EmailContentWithSubject

import scala.concurrent.Future

trait EmailService {

  def scheduleEmail(address: String, emailData: EmailContentWithSubject): Future[Unit]

}
