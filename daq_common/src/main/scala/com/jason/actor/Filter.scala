package com.jason.actor

import akka.actor._
import com.jason.actor.Controller._
import com.jason.actor.Statistic._
import com.jason.core.BackfillerPluginFacade
import com.jason.model.Phase
import com.jason.utils.RetryLogic._
import com.jason.utils.TimeUtil._
import com.jason.actor.Converter._
import com.jason.actor.Slice._

object Filter {

  case class RequestFilter(args: Seq[Any])

  def props(plugin: BackfillerPluginFacade[_], controller: ActorRef, converter: ActorRef, statistic: ActorRef) =
    Props(new Filter(plugin, controller, converter, statistic))
}

/** Filter is take care remove duplicate rule.*/
class Filter(plugin: BackfillerPluginFacade[_], controller: ActorRef, converter: ActorRef, statistic: ActorRef)
  extends Actor with ActorLogging with ReSubmit{

  import Filter._

  def _receive: Receive = {
    case StartFilter =>
      sender ! StartFilter

    case RequestFilter(args) =>
      val provider = plugin.filterProvider
      val (time, filterRes) = timer {
        retry(args, provider.filter, Phase.Filter, plugin.exceptionHandler)
      }
      statistic ! RecordFilterTime(time)

      filterRes.fold(statistic ! RecordFilterFailure){ res =>
        statistic ! RecordFilter(args.size, res.size)
        if(res.nonEmpty) converter ! RequestConverter(res)
        else {
          log.debug("after filter, the size is empty, request slice again")
          controller ! RequestSlice(Phase.Filter)
        }
      }

    case ShutDown=>
      sender ! FilterComplete
      context.stop(self)
  }
}

