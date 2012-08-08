package com.derbysoft.dswitch.router.client.rule

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.Http
import org.jboss.netty.util.CharsetUtil
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import org.jboss.netty.buffer.ChannelBuffers

object RuleHttpClient {

  def apply(hosts: String): RuleHttpClient = {
    new RuleHttpClient(hosts)
  }

}

class RuleHttpClient(host: String) {

  def get(): Future[HttpResponse] = {
    val client = ClientBuilder().codec(Http()).hosts(host).hostConnectionLimit(5).hostConnectionCoresize(3).build()
    val future: Future[HttpResponse] = client(defaultHttpRequest(HttpMethod.GET)) onSuccess {
      response => printResponse(response)
    } onFailure {
      error => println("Get rules error:" + error)
    }
    future.ensure {
      client.release()
    }
  }

  def post(rules: String): Future[HttpResponse] = {
    val clientWithoutErrorHandling = ClientBuilder().codec(Http()).hosts(host).hostConnectionLimit(1).build()
    val handleErrors = new HandleErrors
    val client: Service[HttpRequest, HttpResponse] = handleErrors andThen clientWithoutErrorHandling
    val request = makeAuthorizedRequest(rules, client)
    request ensure {
      client.release()
    }
  }

  class InvalidRequest extends Exception

  class HandleErrors extends SimpleFilter[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]) = {
      service(request) flatMap {
        response =>
          response.getStatus match {
            case OK => Future.value(response)
            case FORBIDDEN => Future.exception(new InvalidRequest)
            case _ => Future.exception(new Exception(response.getStatus.getReasonPhrase))
          }
      }
    }
  }

  private def defaultHttpRequest(method: HttpMethod): DefaultHttpRequest = {
    new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, "/rule")
  }

  private def printResponse(response: HttpResponse) {
    println("response : " + response.getContent.toString(CharsetUtil.UTF_8))
  }

  private def makeAuthorizedRequest(rules: String, client: Service[HttpRequest, HttpResponse]) = {
    val request = defaultHttpRequest(HttpMethod.POST)
    request.setHeader("Authorization", "DerbysoftRouterRule")
    request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
    request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
    request.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/x-www-form-urlencoded");
    val content = ChannelBuffers.copiedBuffer("rules=" + rules, CharsetUtil.UTF_8)
    request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());
    request.setContent(content)
    client(request) onSuccess {
      response => printResponse(response)
    } onFailure {
      error => println("Save rules error:" + error)
    }
  }
}
