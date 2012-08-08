package com.derbysoft.dswitch.router.util


object JavaTimer {

  val timer = new com.twitter.util.JavaTimer()

  def apply(): com.twitter.util.JavaTimer = {
    timer
  }

}
