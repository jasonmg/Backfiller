package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.core._

/**
  * Source response for read data from different resource system
  */
class Source(plugin: BaseBackfillerPlugin) extends Actor with ActorLogging {
import Source._

  def receive = {
    case StartSource =>
      log.info("start source actor.")
      sender() ! StartSourceDone
    case RequestSource =>
      log.info(s"request source from: ${plugin.getClass.getName}")
      plugin.sourceProvider
  }
}

object Source{
  def props(plugin: BaseBackfillerPlugin) =
    Props(new Source(plugin))


  case object RequestSource
}