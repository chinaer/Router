package com.derbysoft.dswitch.router.core

import com.derbysoft.dswitch.router.util.RouterUrl
import scala._

trait ResponseStatus {
  def hasError: Boolean
}

case class Successful() extends ResponseStatus {
  override def hasError: Boolean = {
    false
  }
}

case class Error(val source: String, val code: String, val msg: String) extends ResponseStatus {
  override def hasError: Boolean = {
    true
  }

  override def toString(): String = {
    "Error Source:" + source + ", " + "Error Message:" + code + "-" + msg
  }
}

trait Message

class MessageBody(val uri: String, val taskId: String, val source: String, val destination: String, val extensions: String, val body: Any) extends Serializable {

  def hostKeys(): List[String] = {
    val shortUri = RouterUrl.getShort(uri)
    val key1 = join(source, destination, uri)
    val key2 = join(source, destination, shortUri)
    val key3 = join(source, destination)
    val key4 = join(destination, uri)
    val key5 = join(destination, shortUri)
    val key6 = destination
    List(key1, key2, key3, key4, key5, key6)
  }

  private def join(s1: String, s2: String, s3: String): String = {
    join(join(s1, s2), s3)
  }

  private def join(s1: String, s2: String): String = {
    s1 + RouterUrl.separator + s2
  }


}

case class RequestMessage(message: MessageBody) extends Message

case class ResponseMessage(val message: MessageBody, val status: ResponseStatus) extends Message {

}

