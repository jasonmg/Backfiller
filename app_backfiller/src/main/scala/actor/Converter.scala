package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.core._
import main.scala.utils.RetryLogic._

/**
  * Converter response for convert source data into desired data type
  */
class Converter(plugin: BaseBackfillerPlugin[_], sinkActor: ActorRef) extends Actor with ActorLogging {

  import Converter._

  def receive = {
    case StartConverter =>
      log.info("start converter actor ")
      sender() ! StartConverterDone

    case RequestConverter(arg) =>
      val res = retry(arg, plugin.convertProvider.convert)
    //      sinkActor !
  }

}

object Converter {
  def props(plugin: BaseBackfillerPlugin[_], sinkActor: ActorRef) = {
    Props(new Converter(plugin, sinkActor))
  }

  case class RequestConverter(arg: Seq[Entity])

}
