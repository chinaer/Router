package com.derbysoft.dswitch.router.core.reservation

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import com.derbysoft.dswitch.router.core.{ResponseMessage, RequestMessage}
import com.derbysoft.dswitch.router.util.{RichFile, ObjectToJson}

object ReservationToFileSender {

  val separator = "."

  implicit def enrichFile(file: String) = new RichFile(file)

  val dateFormat = new SimpleDateFormat("yyyyMMdd")

  private def apply(value: String, fileName: String) = {
    val dir = "reservation/" + dateFormat.format(new Date()) + "/"
    new File(dir).mkdirs()
    RichFile.writeStringToFile(value, dir + fileName)
  }

  def request(request: RequestMessage) = {
    ReservationToFileSender(ReservationHelper.replaceCardNumber(ObjectToJson(request.message.body)), request.message.taskId + separator + Reservation.rq)
  }

  def response(response: ResponseMessage) = {
    val message = response.message
    if (response.status.hasError) {
      ReservationToFileSender(response.status.toString, response.message.taskId + separator + Reservation.error)
    } else {
      ReservationToFileSender(ReservationHelper.replaceCardNumber(ObjectToJson(message.body)), message.taskId + separator + Reservation.rs)
    }
  }

}

