package com.derbysoft.dswitch.router.core.rule

import com.twitter.finagle.{Service, SimpleFilter}
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.util.CharsetUtil.UTF_8
import com.twitter.util.Future
import java.net.InetSocketAddress
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.Http
import org.jboss.netty.util.CharsetUtil
import collection.mutable.Map
import com.derbysoft.dswitch.router.util.{RichFile, MapToProperties, PropertiesToMap}
import com.derbysoft.dswitch.router.core.Config

class RuleHttpServer(port: Int, hosts: Map[String, String], fileName: String) {

  private def validateRequest = false

  def start() {
    val handleExceptions = new HandleExceptions
    val authorize = new Authorize
    val respond = new Respond
    val service: Service[HttpRequest, HttpResponse] = handleExceptions andThen authorize andThen respond
    ServerBuilder().codec(Http()).bindTo(new InetSocketAddress(port))
      .requestTimeout(Config.twoMinutes)
      .hostConnectionMaxIdleTime(Config.twoMinutes)
      .name("RuleHttpServer").build(service)
  }

  class Respond extends Service[HttpRequest, HttpResponse] {

    def apply(request: HttpRequest) = {
      if (request.getMethod == HttpMethod.GET) {
        get
      } else if (request.getMethod == HttpMethod.POST) {
        post(request)
      }
      else {
        Future.value(createResponse("Unsupported Operation."))
      }
    }

    def get: Future[DefaultHttpResponse] = {
      val json = MapToProperties(hosts.toMap[String, String])
      Future.value(createResponse(json))
    }

    def post(request: HttpRequest): Future[DefaultHttpResponse] = {
      val decoder = new QueryStringDecoder("?" + request.getContent.toString(CharsetUtil.UTF_8));
      val value = decoder.getParameters().get("rules").get(0)
      val hostMap = PropertiesToMap.stringToMap(value)
      //TODO validate
      validate(hostMap)
      val hostList = hostMap.toList
      hosts.clear()
      hosts ++= hostList
      RichFile.writeStringToFile(value, fileName)
      get
    }

    def validate(hostMap: scala.collection.immutable.Map[String, String]) {
      val contains = hostMap.keySet.contains("reservation.redis.host")
      if (!contains) {
        throw new IllegalArgumentException("reservation.redis.host is required.")
      }
    }

    def createResponse(content: String): DefaultHttpResponse = {
      val response = new DefaultHttpResponse(HTTP_1_1, OK)
      response.setContent(copiedBuffer(content, UTF_8))
      response
    }
  }

  class HandleExceptions extends SimpleFilter[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]) = {
      service(request) handle {
        case error =>
          val statusCode = error match {
            case _: IllegalArgumentException =>
              FORBIDDEN
            case _ =>
              INTERNAL_SERVER_ERROR
          }
          val errorResponse = new DefaultHttpResponse(HTTP_1_1, statusCode)
          errorResponse.setContent(copiedBuffer("Error:" + error.getMessage, UTF_8))
          errorResponse
      }
    }
  }

  class Authorize extends SimpleFilter[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest, continue: Service[HttpRequest, HttpResponse]) = {
      if (request.getUri.equals("/rule")) {
        if (validateRequest) {
          if ("DerbysoftRouterRule" == request.getHeader("Authorization")) {
            continue(request)
          } else {
            Future.exception(new UnsupportedOperationException("You do not have permission to access this page."))
          }
        }
        else {
          continue(request)
        }
      }
      else {
        Future.exception(new IllegalArgumentException("404. The page is not found."))
      }

    }
  }

}