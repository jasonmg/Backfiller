package main.scala.actor

import java.util

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import main.scala.core.{BackfillerArgs, BaseBackfillerPlugin}
import main.scala.actor.Controller.{StartSlice, _}
import main.scala.actor.Source.RequestSource
import main.scala.utils.RetryLogic._
import scala.collection.JavaConverters._

class Slice[CmdLineArgs <: BackfillerArgs](plugin: BaseBackfillerPlugin[CmdLineArgs], controllerActor: ActorRef, sourceActor: ActorRef) extends Actor with ActorLogging {

  import Slice._

  def receive = {
    case StartSlice => sender() ! StartSliceDone
    case RequestSlice =>
      val sliceRes = plugin.sliceProvider.slice(plugin.cmdLine)
      sliceRes foreach { res => sourceActor ! RequestSource(res) }
  }
}

object Slice {

  def props[CmdLineArgs <: BackfillerArgs](plugin: BaseBackfillerPlugin[CmdLineArgs], controllerActor: ActorRef, sourceActor: ActorRef) = {
    Props(new Slice(plugin, controllerActor, sourceActor))
  }

  case object RequestSlice

}
