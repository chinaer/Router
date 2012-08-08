package com.derbysoft.dswitch.router.core.reservation

import com.derbysoft.dswitch.router.core.rule.Rule
import com.derbysoft.dswitch.router.core.{ResponseMessage, RequestMessage}
import com.derbysoft.dswitch.router.util.ObjectToJson

class ReservationToRedisSender(rule: Rule) {

  private val redis = Redis(rule.reservationCenterHost())

  def request(request: RequestMessage) {
    val message = request.message
    val field = Reservation.rq + "-" + request.message.source + "-" + request.message.destination
    redis.hset(message.taskId, field, ReservationHelper.replaceCardNumber(ObjectToJson(message.body)))
  }

  def response(response: ResponseMessage) {
    val message = response.message
    if (response.status.hasError) {
      redis.hset(message.taskId, Reservation.error, response.status.toString)
    } else {
      redis.hset(message.taskId, Reservation.rs, ReservationHelper.replaceCardNumber(ObjectToJson(message.body)))
    }
  }

}