package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.core._

/**
  * Sink response for persistent data into specific destination
  */
class Sink(plugin: BaseBackfillerPlugin[_]) extends Actor with ActorLogging {
  import Sink._

  def receive = {
    case StartSink =>
      log.info("start sink actor ")
      sender() ! StartSinkDone

    case RequestSink =>
      plugin.sinkProvider
  }

}

object Sink {
  def props(plugin: BaseBackfillerPlugin[_]) = {
    Props(new Sink(plugin))
  }
  case object RequestSink
}