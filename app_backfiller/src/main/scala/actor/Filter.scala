package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.actor.Converter.RequestConverter
import main.scala.core.BackfillerPluginFacade

/**
  * Created by Administrator on 2016-07-31.
  */
class Filter(plugin: BackfillerPluginFacade[_], converter: ActorRef) extends Actor with ActorLogging {

  import Filter._

  def receive = {
    case StartFilter =>
      sender ! StartFilter

    case RequestFilter(args) =>
      val filter = plugin.filterProvider
      val filterRes = filter.filter(args)
      converter ! RequestConverter(filterRes)

    case ShutDown=>
      sender ! FilterComplete
      context.stop(self)
  }
}

object Filter {

  case class RequestFilter(args: Seq[Any])

  def props(plugin: BackfillerPluginFacade[_], converter: ActorRef) = Props(new Filter(plugin, converter))
}
