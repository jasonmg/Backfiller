package com.jason.actor

import akka.actor._
import com.jason.actor.Controller._
import com.jason.actor.Statistic._
import com.jason.core.{BackfillerArgs, BackfillerPluginFacade}
import com.jason.model.Phase
import com.jason.core.BackfillerPluginFacade
import com.jason.model.Phase.Phase
import com.jason.utils.RetryLogic._
import com.jason.utils.TimeUtil._
import com.jason.actor.Source._
import scala.collection.mutable

object Slice {

  def props[CmdLineArgs <: BackfillerArgs](plugin: BackfillerPluginFacade[CmdLineArgs], controller: ActorRef, source: ActorRef, statistic: ActorRef) = {
    Props(new Slice(plugin, controller, source, statistic))
  }

  case class RequestSlice(from: Phase = Phase.Slice)

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
    case StartSlice => sender ! RequestSlice(Phase.Slice)

    case ScheduleShutDown(length) =>
      log.info(s"receive ScheduleShutDown message after run: $length, shutdown slice actor")
      stop()

    case RequestSlice(_) =>
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
              log.info(s"sliced sequence size is: ${res.size}")
              enqueueFn(res)
            }
          }
        }
        statistic ! RecordSliceTime(time)
        sliced = true
      }

      if (workQueue.nonEmpty) {
        log.debug(s"workQueue size: ${workQueue.size}")
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
    case RequestSlice(_) =>
      log.info(s"wait for terminate, can't accept any request")
    case Controller.ShutDown =>
      controller ! Controller.ShutDown
      context.stop(self)
  }
}

