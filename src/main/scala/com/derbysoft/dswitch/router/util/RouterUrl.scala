package com.derbysoft.dswitch.router.util

object RouterUrl {

  val ping = "hotel.ping.ping"

  val availNormal = "hotel.avail.normal"

  val availNoCache = "hotel.avail.nocache"

  val resInitiate = "hotel.reservation.initiate"

  val resBook = "hotel.reservation.book"

  val resCancel = "hotel.reservation.cancel"

  val cdsAvailStatusChange = "hotel.cds.availstatus"

  val cdsDailyRateChange = "hotel.cds.ratedaily"

  val cdsLosRateChange = "hotel.cds.ratelos"

  val cdsInventoryChange = "hotel.cds.inventory"

  val separator = "."

  def getShort(url: String): String = {
    val values = url.split("\\"+separator)
    if (values.length != 3) {
      return url;
    }
    return values(0) + separator + values(1)
  }

}
