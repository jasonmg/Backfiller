package com.jason.actor

import akka.actor._
import com.jason.actor.Controller._
import com.jason.actor.Statistic._
import com.jason.core.BackfillerPluginFacade
import com.jason.impl.{BatchSink, SinkStatus}
import com.jason.model.{EntityCollection, Phase}
import com.jason.impl.SinkStatus
import com.jason.model.Phase
import com.jason.actor.Slice._

object Sink {
  def props(plugin: BackfillerPluginFacade[_], batchSize: Int, controller: ActorRef, statistic: ActorRef) = {
    Props(new Sink(plugin, batchSize, controller, statistic))
  }

  case class RequestSink(arg: EntityCollection)

  case object Flush
  case object FlushComplete
}

/**
  * Sink response for persistent data into specific destination
  */
class Sink(plugin: BackfillerPluginFacade[_], batchSize: Int, controller: ActorRef, statistic: ActorRef) extends Actor
  with ActorLogging with ReSubmit {

  import Sink._

  val batchSink = new BatchSink(plugin.sinkProvider, batchSize, new SinkStatus {
    def recordInsert(num: Int) = statistic ! RecordInsert(num)
    def recordFlush(time: Long) = statistic ! RecordFlush(time)
  })

  def _receive: Receive = {
    case StartSink =>
      sender ! StartSink

    case RequestSink(ele) =>
      batchSink.insert(ele)
      controller ! RequestSlice(Phase.Sink)

    case Flush =>
      batchSink.flush()
      log.info("Flush Sink")
      sender ! FlushComplete

    case Controller.ShutDown =>
      log.info("flush before sink actor shutdown")
      batchSink.flush()

      sender ! SinkComplete
      context.stop(self)

  }

}

