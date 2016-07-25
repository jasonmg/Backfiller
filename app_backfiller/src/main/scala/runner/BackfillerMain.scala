package main.scala.runner

import akka.actor.{ActorSystem, Props}
import main.scala.actor.Controller
import main.scala.actor.Controller._
import main.scala.core._
import main.scala.utils.{CmdLineParserBase, magic}
import main.scala.core.BackfillerArgsHandler
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

abstract class BackfillerMain[Args <: BackfillerArgs](implicit e: magic.DefaultTo[Args, BackfillerArgs], val manifest: Manifest[Args]) extends CmdLineParserBase[Args] {
  private val system = ActorSystem("BaseBackfillerSystem")

  override def main(args: Array[String]): Unit = {
    BackfillerArgsHandler.setup()
    super.main(args)
    start
  }

  def start: Unit = {

    val pluginName = cmdLine.pluginName
    val pluginClass = Class.forName(pluginName)
    val plugin = pluginClass.getConstructors().toSeq.head.newInstance(cmdLine).asInstanceOf[BackfillerPlugin[Entity, Args, Any]]

    val BasePlugin = new BaseBackfillerPlugin(plugin, cmdLine)
    val controller = system.actorOf(Props(new Controller(BasePlugin)), "BaseControllerActor")

    controller ! AllStart
    //    controller ! AllComplete
    //    Thread.sleep(5000)


    //    system.stop(controller)
  }

  def onComplete() = {


    system.shutdown()
  }

}
