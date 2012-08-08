package com.derbysoft.dswitch.router.client

import com.derbysoft.dswitch.router.core._
import org.slf4j.LoggerFactory
import com.twitter.util.Future
import com.derbysoft.dswitch.router.core.RequestMessage
import com.derbysoft.dswitch.router.core.ResponseMessage

object Client {

  private var client: Client = null

  def apply(hosts: String): Client = {
    apply(hosts, Config.clientHostConnectionLimit)
  }

  def apply(hosts: String, connectionSize: Int): Client = {
    if (client == null) {
      client = new Client(hosts, connectionSize)
    }
    client
  }

}

class Client(hosts: String, hostConnectionLimit: Int) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val client = ClientPool.create(hosts, hostConnectionLimit)

  def apply(message: RequestMessage): Future[ResponseMessage] = {
    client(message) onFailure {
      error => {
        logger.error(SystemError.getErrorMessage(error))
      }
    }
  }

}