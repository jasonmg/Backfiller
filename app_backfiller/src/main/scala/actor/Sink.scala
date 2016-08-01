package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.core._
import main.scala.model
import main.scala.utils.RetryLogic._
import main.scala.actor.Statistic._

/**
  * Sink response for persistent data into specific destination
  */
class Sink(plugin: BackfillerPluginFacade[_], controller: ActorRef, statistic: ActorRef) extends Actor
  with ActorLogging with ReSubmit {

  import Sink._

  def _receive: Receive = {
    case StartSink =>
      sender ! StartSink

    case RequestSink(ele) =>
      retry(ele, plugin.sinkProvider.insert)
      statistic ! SinkRecord
      controller ! SinkComplete

    case Controller.ShutDown =>
      sender ! SinkComplete
      context.stop(self)

  }

}

object Sink {
  def props(plugin: BackfillerPluginFacade[_], controller: ActorRef, statistic: ActorRef) = {
    Props(new Sink(plugin, controller, statistic))
  }

  case class RequestSink(arg: model.EntityCollection)

}