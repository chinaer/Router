package com.derbysoft.dswitch.router.core

import com.twitter.finagle.{SimpleFilter, Service}
import com.derbysoft.dswitch.dto.common.KeyValue
import com.derbysoft.dswitch.router.util.StopWatch

class ElapsedTimeService extends SimpleFilter[RequestMessage, ResponseMessage] {

  def apply(request: RequestMessage, continue: Service[RequestMessage, ResponseMessage]) = {
    val stopWatch = new StopWatch()
    stopWatch.start()
    continue(request) onSuccess {
      response => {
        stopWatch.stop()
        response.addElapsedTime(new KeyValue("DS", String.valueOf(stopWatch.getTime)))
      }
    }
  }

}
