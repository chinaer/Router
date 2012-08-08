package com.derbysoft.dswitch.router.core.rule

import com.derbysoft.dswitch.router.core.MessageBody

trait Rule {

  // return comma-separated "host1:port1,host2:port2"
  def destinationHosts(message: MessageBody): Option[String]

  // return "host:port"
  def reservationCenterHost(): String

}