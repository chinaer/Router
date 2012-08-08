package com.derbysoft.dswitch.router.core.reservation

import java.util.regex.Pattern

object ReservationHelper {

  def replaceCardNumber(request: String): String = {
    if (request == null || request.equals("")) {
      return request;
    }
    val cardNumber = getAttributeMatch(request, "cardNumber\":\"");
    if (cardNumber != null && !cardNumber.equals("")) {
      return request.replace(cardNumber, "......");
    }
    return request;
  }

  def getAttributeMatch(request: String, start: String): String = {
    return getMatch(request, start, "\"");
  }

  private def getMatch(request: String, start: String, end: String): String = {
    val m = Pattern.compile("(" + start + ")(.+?)(" + end + ")").matcher(request);
    if (m.find()) {
      return m.group(2);
    }
    return "";
  }


}
