package com.derbysoft.dswitch.router.core


object SystemError {

  def apply(message: MessageBody, e: Throwable): ResponseMessage = {
    val body = new MessageBody(message.uri, message.taskId, message.source, message.destination, message.extensions, getErrorMessage(e))
    val error = new Error(message.source + "-Dswitch", "System", getErrorMessage(e))
    ResponseMessage(body, error)
  }

  def getErrorMessage(error: scala.Throwable): String = {
    var t = error.getCause
    if (t == null) {
      t = error
    }
    "errorMessage:\n" + t.getClass.getName + ":" + t.getMessage + "\n errorStackTrace:\n" + t.getStackTraceString
  }

}

object TimeOutError {
  def apply(message: MessageBody): ResponseMessage = {
    val body = new MessageBody(message.uri, message.taskId, message.source, message.destination, message.extensions, "TimeoutException")
    val error = new Error(message.source + "-Dswitch", "Timeout", "timeout")
    ResponseMessage(body, error)
  }

}

object OverLoadError {
  def apply(message: MessageBody): ResponseMessage = {
    val body = new MessageBody(message.uri, message.taskId, message.source, message.destination, message.extensions, "OverLoadException")
    val error = new Error(message.source, "OverLoad", "OverLoad")
    ResponseMessage(body, error)
  }

}

object NullResponseError {
  def apply(message: MessageBody, timeout: Long): ResponseMessage = {
    val body = new MessageBody(message.uri, message.taskId, message.source, message.destination, message.extensions, "TimeOutException")
    val error = new Error(message.source + "-Dswitch", "TimeOut", "response did not return on time[" + timeout + "s].")
    ResponseMessage(body, error)
  }

}


