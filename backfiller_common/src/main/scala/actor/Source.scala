package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.actor.Filter.RequestFilter
import main.scala.core._
import main.scala.utils.RetryLogic._
import main.scala.actor.Statistic._

/**
  * Source response for read data from diverse resource system
  */
class Source(plugin: BackfillerPluginFacade[_], filter: ActorRef, statistic: ActorRef)
  extends Actor with ActorLogging with ReSubmit{

  import Source._

  def _receive: Receive = {
    case StartSource =>
      sender ! StartSource

    case RequestSource(arg) =>
      log.info(s"request source from: ${plugin.getClass.getName}")
      val res = retry(arg, plugin.sourceProvider.load)
      statistic ! SourceRecord
      filter ! RequestFilter(res.toSeq)

    case Controller.ShutDown =>
      sender ! SourceComplete
      context.stop(self)
  }
}

object Source {
  def props(plugin: BackfillerPluginFacade[_], filter: ActorRef, statistic: ActorRef) =
    Props(new Source(plugin, filter, statistic))


  case class RequestSource(sourceArg: Any)

}