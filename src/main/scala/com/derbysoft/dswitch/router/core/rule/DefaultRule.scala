package com.derbysoft.dswitch.router.core.rule

import com.derbysoft.dswitch.router.core.MessageBody

class DefaultRule(hosts: scala.collection.mutable.Map[String, String]) extends Rule {

  override def destinationHosts(message: MessageBody): Option[String] = {
    val keys = message.hostKeys
    keys.foreach(key => {
      hosts.get(key) match {
        case Some(host) => {
          return Some(host)
        }
        case None =>
      }
    })
    None
  }

  override def reservationCenterHost(): String = {
    hosts.get("reservation.redis.host") match {
      case Some(host) => return host
      case None => ""
    }
  }

}
