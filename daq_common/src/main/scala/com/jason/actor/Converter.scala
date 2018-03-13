package com.jason.actor

import akka.actor._
import com.jason.actor.Controller._
import com.jason.actor.Statistic._
import com.jason.core.BackfillerPluginFacade
import com.jason.model.Phase
import com.jason.utils.RetryLogic._
import com.jason.utils.TimeUtil._
import com.jason.actor.Sink._

object Converter {
  def props(plugin: BackfillerPluginFacade[_], sink: ActorRef, statistic: ActorRef) = {
    Props(new Converter(plugin, sink, statistic))
  }

  case class RequestConverter(arg: Seq[Any])
}

/** Converter response for convert source data into desired data type */
class Converter(plugin: BackfillerPluginFacade[_], sink: ActorRef, statistic: ActorRef)
  extends Actor with ActorLogging with ReSubmit{

  import Converter._

  def _receive: Receive = {
    case StartConverter =>
      sender ! StartConverter

    case RequestConverter(arg) =>
      val (time, res) = timer {
        retry(arg, plugin.convertProvider.convert, Phase.Convert, plugin.exceptionHandler)
      }
      statistic ! RecordConvertTime(time)

      res.fold(statistic ! RecordConvertFailure) { r =>
        sink ! RequestSink(r)
        statistic ! RecordConvert
      }

    case Controller.ShutDown =>
      sender ! ConverterComplete
      context.stop(self)
  }

}


