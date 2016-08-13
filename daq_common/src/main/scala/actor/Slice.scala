package main.scala.actor

import akka.actor._
import com.codahale.metrics.Clock
import main.scala.core.{BackfillerArgs, BackfillerPluginFacade}
import main.scala.actor.Controller._
import main.scala.actor.Source.RequestSource
import main.scala.utils.RetryLogic._
import main.scala.actor.Statistic._
import main.scala.model.Phase
import main.scala.utils.TimeUtil._

import scala.collection.mutable

object Slice {

  def props[CmdLineArgs <: BackfillerArgs](plugin: BackfillerPluginFacade[CmdLineArgs], controller: ActorRef, source: ActorRef, statistic: ActorRef) = {
    Props(new Slice(plugin, controller, source, statistic))
  }

  case object RequestSlice

  case object AllSliceSent

}

/** Slice is for split source from multiple chunk, this is because:
  * 1. for performance concerns
  * 2. each actor should take as less task as possible
  * 3. use multiple cpu parallel
  */
class Slice[CmdLineArgs <: BackfillerArgs](plugin: BackfillerPluginFacade[CmdLineArgs], controller: ActorRef, source: ActorRef, statistic: ActorRef)
  extends Actor with ActorLogging {

  import Slice._

  val workQueue = new mutable.Queue[Any]()

  def receive: Receive = {
    case StartSlice => sender ! StartSlice

    case RequestSlice =>
      val provider = plugin.sliceProvider
      val (time, sliceRes) = timer {
        retry(plugin.cmdLine, provider.slice, Phase.Slice, plugin.exceptionHandler)
      }
      statistic ! RecordSliceTime(time)

      // enqueueFn is for turn enqueue signature (A*) => Unit to (Seq[A]) => Unit
      // since sliceRes type is Option[Seq[Any]], we'd like explicitly pass slice result one by one to Queue in option.foreach(fun)
      // otherwise enqueue will treat Seq[Any] as an AnyRef insert into queue, that's will cause queue size is 1.
      // be careful scala Currying.
      val enqueueFn = workQueue.enqueue _
      sliceRes foreach enqueueFn    // even it's same with sliceRes foreach workQueue.enqueue
      while (workQueue.nonEmpty) {
        source ! RequestSource(workQueue.dequeue())
        statistic ! RecordSlice
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

