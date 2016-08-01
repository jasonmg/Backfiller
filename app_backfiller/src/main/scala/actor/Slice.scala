package main.scala.actor

import java.util

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import main.scala.core.{BackfillerArgs, BackfillerPluginFacade}
import main.scala.actor.Controller._
import main.scala.actor.Source.RequestSource
import main.scala.utils.RetryLogic._
import scala.collection.JavaConverters._
import main.scala.actor.Statistic._

class Slice[CmdLineArgs <: BackfillerArgs](plugin: BackfillerPluginFacade[CmdLineArgs], controller: ActorRef, source: ActorRef, statistic: ActorRef)
  extends Actor with ActorLogging with ReSubmit {

  import Slice._

  val workQueue = new java.util.ArrayDeque[Seq[_]]()

  def _receive: Receive = {
    case StartSlice => sender ! StartSlice

    case RequestSlice =>
      val sliceRes = plugin.sliceProvider.slice(plugin.cmdLine)
      sliceRes foreach { res =>
        source ! RequestSource(res)
        statistic ! SliceRecord
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

object Slice {

  def props[CmdLineArgs <: BackfillerArgs](plugin: BackfillerPluginFacade[CmdLineArgs], controller: ActorRef, source: ActorRef, statistic: ActorRef) = {
    Props(new Slice(plugin, controller, source, statistic))
  }

  case object RequestSlice

  case object AllSliceSent

}
