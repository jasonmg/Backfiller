package com.jason.actor

import akka.actor._
import com.jason.actor.Controller._
import com.jason.actor.Statistic._
import com.jason.core.BackfillerPluginFacade
import com.jason.model.Phase
import com.jason.utils.RetryLogic._
import com.jason.utils.TimeUtil._
import com.jason.actor.Filter.RequestFilter


object Source {
  def props(plugin: BackfillerPluginFacade[_], filter: ActorRef, statistic: ActorRef) =
    Props(new Source(plugin, filter, statistic))

  case class RequestSource(sourceArg: Any)

}

/** Source response for read data from diverse resource system */
class Source(plugin: BackfillerPluginFacade[_], filter: ActorRef, statistic: ActorRef)
  extends Actor with ActorLogging with ReSubmit{

  import Source._

  def _receive: Receive = {
    case StartSource =>
      sender ! StartSource

    case RequestSource(arg) =>
      val provider = plugin.sourceProvider
      val (time, res) = timer {
        retry(arg, provider.load, Phase.Source, plugin.exceptionHandler)
      }
      statistic ! RecordSourceTime(time)

      res.fold(statistic ! RecordSourceFailure){ r =>
        filter ! RequestFilter(r)
        statistic ! RecordSource
      }

    case Controller.ShutDown =>
      sender ! SourceComplete
      context.stop(self)
  }
}

