package main.scala.runner

import akka.actor.{ActorSystem, Props}
import main.scala.actor.Controller
import main.scala.actor.Controller._
import main.scala.core._
import main.scala.utils.{CmdLineParserBase, magic}

abstract class BackfillerMain[Args <: BackfillerArgs](implicit e: magic.DefaultTo[Args, BackfillerArgs], val manifest: Manifest[Args]) extends CmdLineParserBase[Args] {
  private val system = ActorSystem("BaseBackfillerSystem")

  override def main(args: Array[String]): Unit = {
    BackfillerArgs.setup()
    super.main(args)
    start
  }

  def start: Unit = {

    val pluginName = cmdLine.pluginName

    val plugin = Class.forName(pluginName).newInstance().asInstanceOf[BackfillerPlugin[Entity, Args, Any]]

    val BasePlugin = new BaseBackfillerPlugin(plugin, cmdLine)
    val controller = system.actorOf(Props(new Controller(BasePlugin)), "BaseControllerActor")

    controller ! AllStart
    controller ! AllComplete
    Thread.sleep(5000)


    system.stop(controller)
    system.shutdown()

  }

  def onComplete() = ???

}
