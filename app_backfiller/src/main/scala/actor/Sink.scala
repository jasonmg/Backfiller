package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.core._
import main.scala.utils.RetryLogic._

/**
  * Sink response for persistent data into specific destination
  */
class Sink(plugin: BaseBackfillerPlugin[_], controllerActor: ActorRef) extends Actor with ActorLogging {

  import Sink._

  def receive = {
    case StartSink =>
      log.info("start sink actor ")
      sender() ! StartSinkDone

    case RequestSink(ele) =>
      retry(ele, plugin.sinkProvider.insert)
      controllerActor ! SinkComplete

  }

}

object Sink {
  def props(plugin: BaseBackfillerPlugin[_], controllerActor: ActorRef) = {
    Props(new Sink(plugin, controllerActor))
  }

  case class RequestSink(arg: EntityCollection)
  case object SinkComplete

}