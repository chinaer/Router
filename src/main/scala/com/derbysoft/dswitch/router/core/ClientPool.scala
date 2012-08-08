package com.derbysoft.dswitch.router.core

import com.twitter.finagle.Service
import com.twitter.finagle.builder.ClientBuilder
import java.util.HashMap
import org.slf4j.LoggerFactory

object ClientPool extends ClientPool

class ClientPool {

  private val map = new HashMap[String, Service[RequestMessage, ResponseMessage]]

  private val logger = LoggerFactory.getLogger(this.getClass)

  def get(hosts: String): Service[RequestMessage, ResponseMessage] = {
    try {
      val service = map.get(hosts)
      if (service != null) {
        return service
      }
      val clientService: Service[RequestMessage, ResponseMessage] = create(hosts)
      map.put(hosts, clientService)
      return clientService
    } catch {
      case e: Exception => {
        logger.error(SystemError.getErrorMessage(e))
        return null
      }
    }
  }

  def create(hosts: String, hostConnectionLimit: Int): Service[RequestMessage, ResponseMessage] = {
    ClientBuilder().codec(MessageCodec).hosts(hosts)
      .tcpConnectTimeout(Config.connectTimeout)
      .hostConnectionMaxIdleTime(Config.twoMinutes)
      .hostConnectionCoresize(Config.clientHostConnectionCoreSize)
      .hostConnectionLimit(hostConnectionLimit)
      .build()
  }

  def create(hosts: String): Service[RequestMessage, ResponseMessage] = {
    create(hosts, Config.clientHostConnectionLimit)
  }
}
