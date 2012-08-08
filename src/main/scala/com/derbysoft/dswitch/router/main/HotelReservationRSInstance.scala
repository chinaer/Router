package com.derbysoft.dswitch.router.main

import com.derbysoft.dswitch.dto.hotel.reservation.{HotelReservationDTO, HotelReservationRS}
import com.derbysoft.dswitch.dto.hotel.common.CancelPolicyDTO


object HotelReservationRSInstance {

  def apply(): HotelReservationRS = {
    val response = new HotelReservationRS()
    response.setCancelPolicy(createCancelPolicy)
    response.setHotelReservation(createHotelReservation)
    response.setErsp("ersp no")
    response
  }

  private def createHotelReservation(): HotelReservationDTO = {
    null
  }

  private def createCancelPolicy(): CancelPolicyDTO = {
    null
  }

}
