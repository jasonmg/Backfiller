package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.actor.Sink.RequestSink
import main.scala.core._
import main.scala.utils.RetryLogic._

/**
  * Converter response for convert source data into desired data type
  */
class Converter(plugin: BackfillerPluginFacade[_], sink: ActorRef) extends Actor with ActorLogging {

  import Converter._

  def receive = {
    case StartConverter =>
      sender ! StartConverter

    case RequestConverter(arg) =>
      val res = retry(arg, plugin.convertProvider.convert)
      sink ! RequestSink(res)

    case Controller.ShutDown =>
      sender ! ConverterComplete
      context.stop(self)
  }

}

object Converter {
  def props(plugin: BackfillerPluginFacade[_], sink: ActorRef) = {
    Props(new Converter(plugin, sink))
  }

  case class RequestConverter(arg: Seq[Any])

}
