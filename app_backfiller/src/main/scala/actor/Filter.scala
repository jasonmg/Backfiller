package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.actor.Converter.RequestConverter
import main.scala.core.BackfillerPluginFacade
import main.scala.actor.Statistic._

class Filter(plugin: BackfillerPluginFacade[_], converter: ActorRef, statistic: ActorRef) extends Actor with ActorLogging {

  import Filter._

  def receive = {
    case StartFilter =>
      sender ! StartFilter

    case RequestFilter(args) =>
      val filter = plugin.filterProvider
      val filterRes = filter.filter(args)
      statistic ! FilterRecord(args.size, filterRes.size)
      converter ! RequestConverter(filterRes)

    case ShutDown=>
      sender ! FilterComplete
      context.stop(self)
  }
}

object Filter {

  case class RequestFilter(args: Seq[Any])

  def props(plugin: BackfillerPluginFacade[_], converter: ActorRef, statistic: ActorRef) =
    Props(new Filter(plugin, converter, statistic))
}
