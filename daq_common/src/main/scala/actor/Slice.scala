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

/** Slice is for split source from multiple chunk, this is because:
  *  1. for performance concerns
  *  2. each acotr should take as less task as possible
  *  3. use multiple cpu parallel
  */
class Slice[CmdLineArgs <: BackfillerArgs](plugin: BackfillerPluginFacade[CmdLineArgs], controller: ActorRef, source: ActorRef, statistic: ActorRef)
  extends Actor with ActorLogging {

  import Slice._

  val workQueue = new mutable.Queue[Any]()

  def receive: Receive = {
    case StartSlice => sender ! StartSlice

    case RequestSlice =>
      val provider = plugin.sliceProvider
      val sliceRes = retry(plugin.cmdLine, provider.slice, Phase.Slice, plugin.exceptionHandler)
      sliceRes foreach workQueue.enqueue

      while (workQueue.nonEmpty) {
        source ! RequestSource(workQueue.dequeue())
        statistic ! SliceRecord
      }

      if (workQueue.isEmpty) {
        controller ! AllSliceSent
        context.become(awaitTerminate)
      }
  }

  def awaitTerminate: Receive = {
    case RequestSlice =>
      log.info(s"wait for terminate, can't accept any request")
    case Controller.ShutDown =>
      controller ! Controller.ShutDown
      context.stop(self)
  }
}

