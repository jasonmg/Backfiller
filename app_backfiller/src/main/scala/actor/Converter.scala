package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.core._

/**
  * Converter response for convert source data into desired data type
  */
class Converter(plugin: BaseBackfillerPlugin) extends Actor with ActorLogging {
  import Converter._

  def receive = {
    case StartConverter =>
      log.info("start converter actor ")
      sender() ! StartConverterDone

    case RequestConverter =>
      plugin.convertProvider
  }

}

object Converter {
  def props(plugin: BaseBackfillerPlugin) = {
    Props(new Converter(plugin))
  }

  case object RequestConverter
}
