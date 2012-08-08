package com.derbysoft.dswitch.router.client

import java.util.concurrent.TimeUnit._
import concurrent.forkjoin.{LinkedTransferQueue, TransferQueue}
import com.derbysoft.dswitch.router.core._
import com.twitter.finagle.TimeoutException
import com.derbysoft.dswitch.router.util.StopWatch

class SyncClient(hosts: String, connectionSize: Int, timeout: Int) {

  private val client = Client(hosts, connectionSize)

  def send(message: MessageBody): ResponseMessage = {
    ThreadStopWatch.start
    val queue = MessageDispatcher.register()
    try {
      client(new RequestMessage(message)) onSuccess {
        response => {
          MessageDispatcher.dispatch(queue, response)
        }
      } onFailure {
        case e: TimeoutException => MessageDispatcher.dispatch(queue, TimeOutError(message))
        case e: Exception => MessageDispatcher.dispatch(queue, SystemError(message, e))
      }
    } catch {
      case e: Exception => MessageDispatcher.dispatch(queue, SystemError(message, e))
    }
    var response = queue.poll(timeout, SECONDS)
    if (response == null) {
      response = NullResponseError(message, timeout)
    }
    println(ThreadStopWatch.stopAndGetTime)
    return response
  }

}

private object MessageDispatcher {

  def dispatch(queue: TransferQueue[ResponseMessage], response: ResponseMessage) = {
    queue.add(response)
  }

  def register(): TransferQueue[ResponseMessage] = {
    new LinkedTransferQueue[ResponseMessage]
  }
}

private object ThreadStopWatch {

  private val stopWatch = new ThreadLocal[StopWatch]

  def start {
    stopWatch.set(new StopWatch())
    stopWatch.get().start()
  }

  def stopAndGetTime: Long = {
    stopWatch.get().stop()
    stopWatch.get().getTime()
  }
}
