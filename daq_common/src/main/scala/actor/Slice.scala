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
  val isContinuous = plugin.isContinuous
  var sliced = false

  def receive: Receive = {
    case StartSlice => sender ! StartSlice

    case ScheduleShutDown(length) =>
      log.info(s"receive ScheduleShutDown message after run: $length, shutdown slice actor")
      stop()

    case RequestSlice =>
      val provider = plugin.sliceProvider
      // enqueueFn is for turn enqueue signature (A*) => Unit to (Seq[A]) => Unit
      // enqueue(_) signature is (Any) => Unit, Be careful scala Currying.
      val enqueueFn = workQueue.enqueue _
      if(workQueue.isEmpty && !sliced){
        val (time, _) = timer {
          tryOpOnce(provider.slice(plugin.cmdLine), Phase.Slice, plugin.exceptionHandler){ res =>
            if (isContinuous) {
              workQueue.enqueue(res.head)
            } else {
              enqueueFn(res)
            }
          }
        }
        statistic ! RecordSliceTime(time)
        sliced = true
      }

      if (workQueue.nonEmpty) {
        source ! RequestSource(getOrLookHead())
        statistic ! RecordSlice
      }

      if (workQueue.isEmpty) {
        stop()
      }
  }

  def getOrLookHead() = if(isContinuous) workQueue.head else workQueue.dequeue()

  def stop() = {
    controller ! AllSliceSent
    context.become(awaitTerminate)
  }

  def awaitTerminate: Receive = {
    case RequestSlice =>
      log.info(s"wait for terminate, can't accept any request")
    case Controller.ShutDown =>
      controller ! Controller.ShutDown
      context.stop(self)
  }
}

