package main.scala.runner

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import main.scala.actor.Controller
import main.scala.actor.Controller._
import main.scala.core._
import main.scala.utils.{CmdLineParserBase, magic}
import main.scala.core.BackfillerArgsHandler
import main.scala.core.continuous.BackfillerContinuousArgs

import scala.concurrent.duration.FiniteDuration
import scala.reflect.internal.MissingRequirementError
import main.scala.utils.ConfigUtil.config

abstract class BackfillerMain[Args <: BackfillerArgs](implicit e: magic.DefaultTo[Args, BackfillerArgs], val manifest: Manifest[Args]) extends CmdLineParserBase[Args] {

  override def main(args: Array[String]): Unit = {
    BackfillerArgsHandler.setup()
    super.main(args)
    start()
  }

  def start(): Unit = {

    val system = ActorSystem("BackfillerSystem", config)

    val pluginName = cmdLine.pluginName
    val pluginCompanion = createPlugin(pluginName)

    val plugin = pluginCompanion(cmdLine)

    val pluginFacade = new BackfillerPluginFacade(plugin)

    val backfillerConfig = config.getConfig("backfiller-plugin")
    val batchSize = backfillerConfig.getInt("batch-insert-size")
    val controller = system.actorOf(Props(new Controller(pluginFacade, batchSize)), "controller")

    controller ! AllStart

    if(pluginFacade.isContinuous && cmdLine.isInstanceOf[BackfillerContinuousArgs]){
      val args = cmdLine.asInstanceOf[BackfillerContinuousArgs]
      val duration = args.duration.getOrElse(FiniteDuration(30, TimeUnit.SECONDS))
      controller ! ScheduleShutDown(duration)
    }

    system.awaitTermination()
    onTerminate(pluginFacade)
  }

  def onTerminate(plugin: BackfillerPluginFacade[Args]) = {
    val optionSmoke = isSmokeTest(plugin)
    optionSmoke.foreach { smoke => smoke.persistIntoFile(cmdLine.smokeFile.get, cmdLine.sinkMode) }

    plugin.onComplete

    val hasError = plugin.exceptionHandler match {
      case ex: FailLoggingExceptionHandler =>
        ex.logExceptionIfRequired()
        ex.hasExceptionOccur
      case _ => false
    }

    if (hasError) {
      log.error(s"has error occurred during backfiller process, exit with error")
      System.exit(1)
    }
  }

  def isSmokeTest(plugin: BackfillerPluginFacade[Args]): Option[DefaultSinkProvider] = {
    if (plugin.cmdLine.smokeFile.isDefined && plugin.sinkProvider.isInstanceOf[DefaultSinkProvider])
      Some(plugin.sinkProvider.asInstanceOf[DefaultSinkProvider])
    else None
  }

  def createPlugin(name: String): BackfillerPluginCompanion[Any, Args, Any] = {
    val ru = scala.reflect.runtime.universe
    val m = ru.runtimeMirror(getClass.getClassLoader)

    val clsSymbol = try {
      m.staticClass(name)
    } catch {
      case e: MissingRequirementError => throw new RuntimeException(s"plugin class: $name doesn't exist", e)
    }

    val module = clsSymbol.companion.asModule
    val moduleMirror = m.reflectModule(module)

    val ins = try {
      moduleMirror.instance
    } catch {
      case e: ClassNotFoundException => throw new RuntimeException(s"Plugin: $name doesn't have companion object", e)
    }

    val res = ins match {
      case companion: BackfillerPluginCompanion[Any, Args, Any] =>
        companion
      case _ => throw new RuntimeException(s"plugin companion object doesn't extends BackfillerPluginCompanion")
    }
    res
  }

}
