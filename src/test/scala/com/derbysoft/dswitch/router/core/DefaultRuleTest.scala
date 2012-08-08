package com.derbysoft.dswitch.router.core

import junit.framework._
import com.derbysoft.dswitch.router.util.RouterUrl
import rule.DefaultRule
;


object DefaultRuleTest {
  def suite: Test = {
    val suite = new TestSuite(classOf[DefaultRuleTest]);
    suite
  }

  def main(args: Array[String]) {
    junit.textui.TestRunner.run(suite);
  }
}

class DefaultRuleTest extends TestCase("app") {

  def testDestinationHosts = {

    val hosts = scala.collection.mutable.Map(
      "GTA.CHOICE.hotel.avail.normal" -> "10.200.107.1:90, 10.200.108.1:99",
      "GTA.CHOICE.hotel.avail" -> "10.200.107.1:90",
      "GTA.TDS" -> "10.200.107.1:90, 10.200.108.1:99",
      "HILTON.hotel.avail.normal" -> "10.200.107.1:990, 10.200.108.1:999",
      "HILTON.hotel.avail" -> "10.200.107.1:990",
      "HILTON" -> "localhost:8082,localhost:8083" ,
      "CHOICE.hotel.reservation.book" -> "211.144.87.212:9001" ,
      "CHOICE.hotel.reservation" -> "211.144.87.212:9002" ,
      "CHOICE" -> "211.144.87.212:9003"
    )

    Assert.assertEquals(Some("localhost:8082,localhost:8083"), new DefaultRule(hosts).destinationHosts(new MessageBody(RouterUrl.resBook, "task id", "GTA", "HILTON", "", "")))
    Assert.assertEquals(Some("10.200.107.1:990, 10.200.108.1:999"), new DefaultRule(hosts).destinationHosts(new MessageBody(RouterUrl.availNormal, "task id", "GTA", "HILTON", "", "")))
    Assert.assertEquals(Some("10.200.107.1:990"), new DefaultRule(hosts).destinationHosts(new MessageBody(RouterUrl.availNoCache, "task id", "GTA", "HILTON", "", "")))
    Assert.assertEquals(Some("10.200.107.1:90, 10.200.108.1:99"), new DefaultRule(hosts).destinationHosts(new MessageBody(RouterUrl.resBook, "task id", "GTA", "TDS", "", "")))
    Assert.assertEquals(None, new DefaultRule(hosts).destinationHosts(new MessageBody(RouterUrl.availNormal, "task id", "GTA", "CHOICdE", "", "")))
    Assert.assertEquals(Some("211.144.87.212:9001"), new DefaultRule(hosts).destinationHosts(new MessageBody(RouterUrl.resBook, "task id", "BOOKING", "CHOICE", "", "")))
    Assert.assertEquals(Some("211.144.87.212:9002"), new DefaultRule(hosts).destinationHosts(new MessageBody(RouterUrl.resCancel, "task id", "BOOKING", "CHOICE", "", "")))
    Assert.assertEquals(Some("211.144.87.212:9003"), new DefaultRule(hosts).destinationHosts(new MessageBody(RouterUrl.availNormal, "task id", "BOOKING", "CHOICE", "", "")))
  }


}


