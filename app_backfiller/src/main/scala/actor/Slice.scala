package main.scala.actor

import java.util

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import main.scala.core.{BackfillerArgs, BaseBackfillerPlugin}
import main.scala.actor.Controller.{StartSlice, _}
import main.scala.actor.Source.RequestSource
import main.scala.utils.RetryLogic._
import scala.collection.JavaConverters._

class Slice[CmdLineArgs <: BackfillerArgs](plugin: BaseBackfillerPlugin[CmdLineArgs], controller: ActorRef, sourceActor: ActorRef) extends Actor with ActorLogging {
  import Slice._

  val workQueue = new java.util.ArrayDeque[Seq[_]]()

  def receive = {
    case StartSlice => sender() ! StartSlice
    case RequestSlice =>
      val sliceRes = plugin.sliceProvider.slice(plugin.cmdLine)
      sliceRes foreach { res => sourceActor ! RequestSource(res) }
      controller ! AllSliceSent
      context.become(awaitTerminate)
  }

  def awaitTerminate:Actor.Receive ={
    case RequestSlice =>
      log.info(s"wait for terminate, can't accept any request")
    case Controller.ShutDown =>
      controller ! Controller.ShutDown
      context.stop(self)
  }
}

object Slice {

  def props[CmdLineArgs <: BackfillerArgs](plugin: BaseBackfillerPlugin[CmdLineArgs], controllerActor: ActorRef, sourceActor: ActorRef) = {
    Props(new Slice(plugin, controllerActor, sourceActor))
  }

  case object RequestSlice
  case object AllSliceSent
}
