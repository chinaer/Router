package com.derbysoft.dswitch.router.main

import com.derbysoft.dswitch.router.core._
import com.derbysoft.dswitch.router.util.PropertiesToMap
import rule.{DefaultRule, RuleHttpServer}

object RouterStart {

  def main(args: Array[String]) {
    try {
      if (args.size == 0) {
        throw new IllegalArgumentException("please input file name: java -jar xxxx.jar fileName")
      }
      val fileName = if (!args.isEmpty) args(0) else "hosts.properties"
      val hostsMap = PropertiesToMap(fileName)
      if (hostsMap != None) {
        val map = scala.collection.mutable.Map.empty[String, String]
        map.++=(hostsMap.toList)
        new Router(9002, new DefaultRule(map)).start()
        println("Started router, Port is " + 9002)
        new RuleHttpServer(8888, map, fileName).start()
        println("Started RuleHttpServer, Port is " + 8888)
      }
    } catch {
      case e: Exception => println(SystemError.getErrorMessage(e))
    }
  }
}

object RouterStartTest {

  def main(args: Array[String]) {
    val fileName = "/Users/zhupan/workspace/dswitch3/router/src/main/profiles/dev/hosts.properties"
    val hostsMap = PropertiesToMap(fileName)
    if (hostsMap != None) {
      val map = scala.collection.mutable.Map.empty[String, String]
      map.++=(hostsMap.toList)
      new Router(9002, new DefaultRule(map)).start()
      println("Started router, Port is " + 9002)
      new RuleHttpServer(8888, map, fileName).start()
      println("Started RuleHttpServer, Port is " + 8888)
    }
  }
}
