package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.core._
import main.scala.utils.RetryLogic._

/**
  * Sink response for persistent data into specific destination
  */
class Sink(plugin: BaseBackfillerPlugin[_], controller: ActorRef) extends Actor with ActorLogging {
  import Sink._

  def receive = {
    case StartSink =>
      log.info("start sink actor ")
      sender() ! StartSink

    case RequestSink(ele) =>
      retry(ele, plugin.sinkProvider.insert)
      controller ! SinkComplete

    case Controller.ShutDown =>
      sender() ! SinkComplete
      context.stop(self)

  }

}

object Sink {
  def props(plugin: BaseBackfillerPlugin[_], controller: ActorRef) = {
    Props(new Sink(plugin, controller))
  }

  case class RequestSink(arg: EntityCollection)
}