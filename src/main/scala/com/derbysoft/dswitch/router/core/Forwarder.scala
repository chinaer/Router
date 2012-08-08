package com.derbysoft.dswitch.router.core

import com.twitter.finagle.{SimpleFilter, Service}
import com.twitter.util.FuturePool
import java.util.concurrent.Executors
import com.derbysoft.dswitch.router.util.RouterUrl
import reservation.{ReservationToRedisSender, ReservationToFileSender}
import rule.Rule

class Forwarder(rule: Rule) extends SimpleFilter[RequestMessage, ResponseMessage] {

  private val reservationToRedisSender = new ReservationToRedisSender(rule)

  private val futurePool = FuturePool(Executors.newFixedThreadPool(10))

  private def isReservation(request: RequestMessage): Boolean = {
    val uri = request.message.uri
    uri.contains(RouterUrl.resBook) || uri.contains(RouterUrl.resCancel)
  }

  def apply(request: RequestMessage, continue: Service[RequestMessage, ResponseMessage]) = {
    if (isReservation(request)) {
      futurePool(reservationToRedisSender.request(request))
      futurePool(ReservationToFileSender.request(request))
      continue(request) onSuccess {
        response => {
          futurePool(reservationToRedisSender.response(response))
          futurePool(ReservationToFileSender.response(response))
        }
      } onFailure {
        e => {
          val response = SystemError(request.message, e)
          futurePool(reservationToRedisSender.response(response))
          futurePool(ReservationToFileSender.response(response))
        }
      }
    } else {
      continue(request)
    }
  }

}
