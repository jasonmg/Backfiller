package main.scala.actor

import akka.actor._
import main.scala.core.{BackfillerArgs, BackfillerPluginFacade}
import main.scala.actor.Controller._
import main.scala.actor.Source.RequestSource
import main.scala.utils.RetryLogic._
import main.scala.actor.Statistic._
import main.scala.model.Phase
import scala.collection.mutable

object Slice {

  def props[CmdLineArgs <: BackfillerArgs](plugin: BackfillerPluginFacade[CmdLineArgs], controller: ActorRef, source: ActorRef, statistic: ActorRef) = {
    Props(new Slice(plugin, controller, source, statistic))
  }

  case object RequestSlice

  case object AllSliceSent
}


class Slice[CmdLineArgs <: BackfillerArgs](plugin: BackfillerPluginFacade[CmdLineArgs], controller: ActorRef, source: ActorRef, statistic: ActorRef)
  extends Actor with ActorLogging {

  import Slice._

  val workQueue = new mutable.Queue[Seq[_]]()

  def receive: Receive = {
    case StartSlice => sender ! StartSlice

    case RequestSlice =>
      val provider = plugin.sliceProvider

      val sliceRes = retry(plugin.cmdLine, provider.slice, Phase.Slice, plugin.exceptionHandler)

      sliceRes foreach { res =>
        res.foreach{ r=>
          source ! RequestSource(r)
          statistic ! SliceRecord
        }

      }
      controller ! AllSliceSent
      context.become(awaitTerminate)
  }

  def awaitTerminate: Actor.Receive = {
    case RequestSlice =>
      log.info(s"wait for terminate, can't accept any request")
    case Controller.ShutDown =>
      controller ! Controller.ShutDown
      context.stop(self)
  }
}

