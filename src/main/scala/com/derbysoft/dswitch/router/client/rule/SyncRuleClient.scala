package com.derbysoft.dswitch.router.client.rule

import java.util.concurrent.TimeUnit._
import concurrent.forkjoin.LinkedTransferQueue
import org.slf4j.LoggerFactory
import org.jboss.netty.handler.codec.http.HttpResponse
import com.twitter.util.Future
import org.jboss.netty.util.CharsetUtil
import scala.Predef._
import com.derbysoft.dswitch.router.core.SystemError

class SyncRuleClient(host: String) {

  private val client = RuleHttpClient(host)

  private val logger = LoggerFactory.getLogger(this.getClass)

  def get(): HttpResponse = {
    return execute(client.get())
  }

  def getValue(): String = {
    return get().getContent.toString(CharsetUtil.UTF_8)
  }

  def post(rules: String): HttpResponse = {
    return execute(client.post(rules))
  }

  private def execute(future: Future[HttpResponse]): HttpResponse = {
    val queue = new LinkedTransferQueue[HttpResponse]
    future onSuccess {
      response => {
        queue.add(response)
      }
    } onFailure {
      error => logger.error(SystemError.getErrorMessage(error))
    }
    return queue.poll(60, SECONDS)
  }

}
