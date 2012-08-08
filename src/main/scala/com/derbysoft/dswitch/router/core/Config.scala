package com.derbysoft.dswitch.router.core

import com.twitter.util.Duration
import java.util.concurrent.TimeUnit


object Config {

  val clientHostConnectionCoreSize = 5

  val clientHostConnectionLimit = 100

  val executorCorePoolSize = 20

  val executorMaxChannelMemorySize = 1000

  val executorMaxTotalMemorySize = 50000

  val twoMinutes = Duration.apply(120, TimeUnit.SECONDS)

  val connectTimeout = Duration.apply(5, TimeUnit.SECONDS)

}
