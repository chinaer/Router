package com.derbysoft.dswitch.router.server

import java.net.InetSocketAddress
import com.twitter.finagle.builder.ServerBuilder
import com.derbysoft.dswitch.router.core._
import com.twitter.util.FuturePool
import java.util.concurrent.Executors
import com.twitter.finagle.Service
import com.derbysoft.dswitch.router.util.TaskID

trait ResponseProcessor {

  def process(message: MessageBody): ResponseMessage

}

class Server(port: Int, processor: ResponseProcessor, feedback: ResponseFeedback) {

  val futurePool = FuturePool(Executors.newCachedThreadPool())

  def start() {
    val service = new Service[RequestMessage, ResponseMessage] {
      def apply(request: RequestMessage) = {
        val future = futurePool(execute(request))
        future onFailure {
          error => {
            println(SystemError.getErrorMessage(error))
            if (feedback != null) {
              feedback.error(TaskID.getHeaderTaskId(request.message.taskId), error)
            }
          }
        } onSuccess {
          response => {
            if (feedback != null) {
              feedback.success(TaskID.getHeaderTaskId(request.message.taskId))
            }
          }
        }
      }
    }
    ServerBuilder().codec(MessageCodec).bindTo(new InetSocketAddress(port))
      .hostConnectionMaxIdleTime(Config.twoMinutes)
      .requestTimeout(Config.twoMinutes)
      .name("Server").build(service)
  }

  def execute(request: RequestMessage): ResponseMessage = {
    processor.process(request.message)
  }

}
