package com.derbysoft.dswitch.router.server

trait ResponseFeedback {

  def error(taskID: String, error: java.lang.Throwable)

  def success(taskID: String)

}
