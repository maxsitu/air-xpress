package com.jc.api.order.application

import com.jc.api.order.OrderId
import com.jc.api.order.domain.Order
import com.jc.api.user.UserId

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by walle on 7/10/17.
  */
class OrderService (
    orderDao: OrderDao
)(implicit ec: ExecutionContext){

  def add(order: Order): Future[OrderId] =
    orderDao.add(order)

  def findById(orderId: OrderId): Future[Option[Order]] =
    orderDao.findById(orderId)

  def findByRequestUserId(requestUserId: UserId): Future[Seq[Order]] =
    orderDao.findByRequestUserId(requestUserId)

  def findByAcceptUserId(acceptUserId: UserId): Future[Seq[Order]] =
    orderDao.findByAcceptUserId(acceptUserId)
}
