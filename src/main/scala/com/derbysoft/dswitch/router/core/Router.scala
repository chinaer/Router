package com.derbysoft.dswitch.router.core

import com.twitter.finagle.builder.ServerBuilder
import java.net.InetSocketAddress
import com.twitter.finagle.{SimpleFilter, Service}
import java.util.ArrayList
import com.derbysoft.dswitch.dto.common.KeyValue
import com.twitter.util.Future
import rule.Rule
import scala.Some

class Router(port: Int, rule: Rule) {

  private val forwarder = new Forwarder(rule)

  private val elapsedTimeService = new ElapsedTimeService

  private val handleExceptions = new HandleExceptions

  def start() {
    def createErrorResponse(request: RequestMessage, error: Throwable): Future[ResponseMessage] = {
      val errorMsg = SystemError.getErrorMessage(error)
      println(errorMsg)
      createFutureErrorResponse(request.message, new Error("Dswitch", "System", errorMsg))
    }

    val proxyService = new Service[RequestMessage, ResponseMessage] {

      def apply(request: RequestMessage) = {
        try {
          val hosts = rule.destinationHosts(request.message)
          hosts match {
            case Some(hosts) => {
              val service = ClientPool.get(hosts)
              service(request) onFailure {
                error => {
                  println("Router:" + error)
                  createErrorResponse(request, error)
                }
              }
            }
            case None => createFutureErrorResponse(request.message, new Error("Dswitch", "HostsNotExisted", "HostsNotExisted"))
          }
        }
        catch {
          case error: Exception => {
            println(error)
            createErrorResponse(request, error)
          }
        }
      }
    }

    val service = handleExceptions andThen elapsedTimeService andThen forwarder andThen proxyService

    ServerBuilder().codec(MessageCodec).bindTo(new InetSocketAddress(port))
      .requestTimeout(Config.twoMinutes)
      .hostConnectionMaxIdleTime(Config.twoMinutes)
      .name("Router").build(service)
  }

  class HandleExceptions extends SimpleFilter[RequestMessage, ResponseMessage] {
    def apply(request: RequestMessage, service: Service[RequestMessage, ResponseMessage]) = {
      service(request) handle {
        case error => {
          val errorMsg = SystemError.getErrorMessage(error)
          println(errorMsg)
          createErrorResponse(request.message, new Error("Dswitch", "System", errorMsg))
        }
      }
    }
  }

  def createFutureErrorResponse(message: MessageBody, error: Error): Future[ResponseMessage] = {
    Future.value(createErrorResponse(message, error))
  }


  def createErrorResponse(message: MessageBody, error: Error): ResponseMessage = {
    val body = new MessageBody(message.uri, message.taskId, message.source, message.destination, message.extensions, None)
    ResponseMessage(body, error, new ArrayList[KeyValue]())
  }
}


