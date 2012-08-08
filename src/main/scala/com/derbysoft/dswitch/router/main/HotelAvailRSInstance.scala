package com.derbysoft.dswitch.router.main

import java.util.ArrayList
import com.derbysoft.dswitch.dto.hotel.avail._
import com.derbysoft.dswitch.dto.hotel.common.{DateRangeDTO, RoomRateDTO, PaymentType, RateDTO}

object HotelAvailRSInstance {

  def apply(): HotelAvailRS = {
    val response = new HotelAvailRS()
    val availRoomStay = new HotelAvailRoomStayDTO()
    availRoomStay.setHotelCode("hotelCode")
    availRoomStay.setRoomStaysList(createRoomStays)
    val availRoomStays: ArrayList[HotelAvailRoomStayDTO] = new ArrayList()
    availRoomStays.add(availRoomStay)
    response.setHotelAvailRoomStaysList(availRoomStays)
    response
  }

  private def crateRatePlan: RatePlanDTO = {
    val ratePlan = new RatePlanDTO()
    ratePlan.setCode("ratePlanCode")
    ratePlan.setName("ratePlanName")
    ratePlan.setPaymentType(PaymentType.POA)
    ratePlan.setDescription("ratePlanDescription")
    ratePlan
  }

  private def createRoomType: RoomTypeDTO = {
    val roomType = new RoomTypeDTO()
    roomType.setName("roomTypeName")
    roomType.setCode("roomTypeCode")
    roomType.setDescription("roomTypeDescription")
    roomType
  }

  private def createRoomRate: RoomRateDTO = {
    val roomRate = new RoomRateDTO()
    val rate = new RateDTO()
    rate.setAmountAfterTax(new java.lang.Double(200))
    rate.setAmountBeforeTax(new java.lang.Double(180))
    val dateRange = new DateRangeDTO()
    dateRange.setStart("2012-10-01")
    dateRange.setEnd("2012-10-05")
    rate.setDateRange(dateRange)
    val rates: ArrayList[RateDTO] = new ArrayList()
    rates.add(rate)
    roomRate.setRatesList(rates)
    roomRate
  }

  private def createRoomStay: AvailRoomStayDTO = {
    val roomStay = new AvailRoomStayDTO()
    roomStay.setCurrency("CNY")
    roomStay.setLanguage("CN")
    roomStay.setQuantity(5)
    roomStay.setRatePlan(crateRatePlan)
    roomStay.setRoomType(createRoomType)
    roomStay.setRoomRate(createRoomRate)
    roomStay
  }

  private def createRoomStays: ArrayList[AvailRoomStayDTO] = {
    val roomStays: ArrayList[AvailRoomStayDTO] = new ArrayList()
    for (i <- 1 to 5) roomStays.add(createRoomStay)
    roomStays
  }

}
