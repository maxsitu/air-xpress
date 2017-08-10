package com.jc.api.flight.application

import com.jc.api.flight.{FlightOrderId, FlightPlanId, FlightStepId}
import com.jc.api.flight.domain.{FlightOrder, FlightPlan, FlightStep}
import com.jc.api.user.UserId

import scala.concurrent.{ExecutionContext, Future}

class FlightService (
  flightDao: FlightDao
)(implicit ec: ExecutionContext){
  /*
  * Endpoints for plan
  * */
  def addPlan(flightPlan: FlightPlan): Future[FlightPlanId] =
    flightDao.addPlan(flightPlan)

  def findPlanByProvideUserId(userId: UserId): Future[Seq[FlightPlan]] =
    flightDao.findPlanByProvideUserId(userId)

  def findPlanByConsumeUserId(userId: UserId): Future[Seq[FlightPlan]] =
    flightDao.findPlanByConsumeUserId(userId)

  def findPlanByInitiateUserId(userId: UserId): Future[Seq[FlightPlan]] =
    flightDao.findPlanByInitiateUserId(userId)

  /*
  * Endpoints for step
  * */
  def addStep(flightStep: FlightStep): Future[FlightStepId] =
    flightDao.addStep(flightStep)

  def findStepByPlanId(planId: FlightPlanId): Future[Seq[FlightStep]] =
    flightDao.findStepByPlanId(planId)

  def findStepByPlanIdSortByFromTime(planId: FlightPlanId) = Future[Seq[FlightStep]] =
    flightDao.findStepByPlanId(planId)

  /*
  * Endpoints for order
  * */
  def addOrder(order: FlightOrder): Future[FlightOrderId] =
    flightDao.addOrder(order)

  def findOrderByPlanId(planId: FlightPlanId): Future[Option[FlightOrder]] =
    flightDao.findOrderByPlanId(planId)
}
