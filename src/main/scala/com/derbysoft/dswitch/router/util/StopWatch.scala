package com.derbysoft.dswitch.router.util

class StopWatch {

  private val nano2millis = 1000000L

  private val stateUnStarted = 0

  private val stateRunning = 1

  private val stateStopped = 2

  private val stateSuspended = 3

  private var runningState = stateUnStarted

  private var startTime = 0l

  private var stopTime = 0l


  def start() {
    if (this.runningState == stateStopped) {
      throw new IllegalStateException("Stopwatch must be reset before being restarted. ")
    }
    if (this.runningState != stateUnStarted) {
      throw new IllegalStateException("Stopwatch already started. ")
    }
    this.startTime = System.nanoTime()
    this.runningState = stateRunning
  }


  def stop() {
    if (this.runningState != stateRunning && this.runningState != stateSuspended) {
      throw new IllegalStateException("Stopwatch is not running. ")
    }
    if (this.runningState == stateRunning) {
      this.stopTime = System.nanoTime()
    }
    this.runningState = stateStopped
  }


  def getTime(): Long = {
    getNanoTime() / nano2millis
  }

  def getNanoTime(): Long = {
    if (this.runningState == stateStopped || this.runningState == stateSuspended) {
      return this.stopTime - this.startTime
    } else if (this.runningState == stateUnStarted) {
      return 0
    } else if (this.runningState == stateRunning) {
      return System.nanoTime() - this.startTime
    }
    throw new RuntimeException("Illegal running state has occured. ")
  }

  def reset() {
    this.runningState = stateUnStarted;
  }

}
