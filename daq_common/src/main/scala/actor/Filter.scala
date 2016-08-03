package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.actor.Converter.RequestConverter
import main.scala.core.BackfillerPluginFacade
import main.scala.actor.Statistic._
import main.scala.model.Phase
import main.scala.utils.RetryLogic._

object Filter {

  case class RequestFilter(args: Traversable[Any])

  def props(plugin: BackfillerPluginFacade[_], converter: ActorRef, statistic: ActorRef) =
    Props(new Filter(plugin, converter, statistic))
}

class Filter(plugin: BackfillerPluginFacade[_], converter: ActorRef, statistic: ActorRef)
  extends Actor with ActorLogging with ReSubmit{

  import Filter._

  def _receive: Receive = {
    case StartFilter =>
      sender ! StartFilter

    case RequestFilter(args) =>
      val provider = plugin.filterProvider
      val filterRes = retry(args, provider.filter, Phase.Filter, plugin.exceptionHandler)
      filterRes.foreach { res =>
        statistic ! FilterRecord(args.size, res.size)
        converter ! RequestConverter(res)
      }


    case ShutDown=>
      sender ! FilterComplete
      context.stop(self)
  }
}

