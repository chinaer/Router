package com.derbysoft.dswitch.router.core

import com.twitter.finagle.{Codec, CodecFactory}
import org.jboss.netty.channel.{ChannelPipelineFactory, Channels}
import org.jboss.netty.handler.codec.serialization._
import org.jboss.netty.handler.execution.{OrderedMemoryAwareThreadPoolExecutor, ExecutionHandler}

object MessageCodec extends MessageCodec

class MessageCodec extends CodecFactory[RequestMessage, ResponseMessage] {

  val handler = new ExecutionHandler(new OrderedMemoryAwareThreadPoolExecutor(Config.executorCorePoolSize, Config.executorMaxChannelMemorySize, Config.executorMaxTotalMemorySize))

  override def server = Function.const {
    new Codec[RequestMessage, ResponseMessage] {
      def pipelineFactory = new ChannelPipelineFactory {
        def getPipeline = {
          val pipeline = Channels.pipeline()
          pipeline.addLast("objectDecoder", new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)))
          pipeline.addLast("objectEncoder", new ObjectEncoder())
          pipeline.addLast("executionHandler", handler)
          pipeline
        }
      }
    }
  }

  override def client = Function.const {
    new Codec[RequestMessage, ResponseMessage] {
      def pipelineFactory = new ChannelPipelineFactory {
        def getPipeline = {
          val pipeline = Channels.pipeline()
          pipeline.addLast("objectEncoder", new ObjectEncoder())
          pipeline.addLast("objectDecoder", new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)))
          pipeline.addLast("executionHandler", handler)
          pipeline
        }
      }
    }
  }
}
