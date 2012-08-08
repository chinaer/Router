package com.derbysoft.dswitch.router.util

import java.util.UUID

object TaskID {

  val SEPARATOR = "_S_"

  def generate: String = {
    return UUID.randomUUID.toString
  }

  def generate(headerTaskId: String): String = {
    return add(generate, headerTaskId)
  }

  def add(routerTaskId: String, headerTaskId: String): String = {
    return routerTaskId + SEPARATOR + headerTaskId
  }

  def split(taskID: String): Array[String] = {
    return taskID.split(SEPARATOR)
  }

  def getRouterTaskId(taskID: String): String = {
    return split(taskID)(0)
  }

  def getHeaderTaskId(taskID: String): String = {
    return split(taskID)(1)
  }

}
