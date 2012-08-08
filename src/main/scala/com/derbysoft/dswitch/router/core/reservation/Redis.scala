package com.derbysoft.dswitch.router.core.reservation

import com.derbysoft.redis.clients.normal.SingleJedis

object Redis {

  var redis: SingleJedis = null;

  def apply(hostAndPort: String): SingleJedis = {
    if (redis == null) {
      return new SingleJedis(hostAndPort)
    }
    redis
  }

}
