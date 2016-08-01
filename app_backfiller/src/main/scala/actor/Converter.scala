package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.actor.Sink.RequestSink
import main.scala.core._
import main.scala.utils.RetryLogic._
import main.scala.actor.Statistic._

/**
  * Converter response for convert source data into desired data type
  */
class Converter(plugin: BackfillerPluginFacade[_], sink: ActorRef, statistic: ActorRef)
  extends Actor with ActorLogging with ReSubmit{

  import Converter._

  def _receive: Receive = {
    case StartConverter =>
      sender ! StartConverter

    case RequestConverter(arg) =>
      val res = retry(arg, plugin.convertProvider.convert)
      statistic ! ConvertRecord
      sink ! RequestSink(res)

    case Controller.ShutDown =>
      sender ! ConverterComplete
      context.stop(self)
  }

}

object Converter {
  def props(plugin: BackfillerPluginFacade[_], sink: ActorRef, statistic: ActorRef) = {
    Props(new Converter(plugin, sink, statistic))
  }

  case class RequestConverter(arg: Seq[Any])

}
