package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.actor.Filter.RequestFilter
import main.scala.core._
import main.scala.utils.RetryLogic._
import main.scala.actor.Statistic._
import main.scala.model.Phase
import main.scala.utils.TimeUtil._

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

