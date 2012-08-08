package com.derbysoft.dswitch.router.util

import com.alibaba.fastjson.serializer.SerializerFeature
import com.alibaba.fastjson.JSON

object ObjectToJson {

  def apply(obj: Any) = {
    JSON.toJSONString(obj, SerializerFeature.QuoteFieldNames)
  }

}
