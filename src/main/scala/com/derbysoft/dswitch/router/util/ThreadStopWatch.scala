package com.derbysoft.dswitch.router.util

object ThreadStopWatch {

  private val stopWatch = new ThreadLocal[StopWatch]

  def start {
    stopWatch.set(new StopWatch())
    stopWatch.get().start()
  }

  def stop = {
    stopWatch.get().stop()
  }

  def getTime: Long = {
    stopWatch.get().getTime()
  }

  def stopAndGetTime: Long = {
    stop
    getTime
  }

}
