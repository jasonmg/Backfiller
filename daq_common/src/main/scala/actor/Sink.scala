package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.core._
import main.scala.model.EntityCollection
import main.scala.utils.RetryLogic._
import main.scala.actor.Statistic._
import main.scala.impl.{BatchSink, SinkStatus}

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
      controller ! StartSlice

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

