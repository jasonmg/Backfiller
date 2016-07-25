package main.scala.core

import main.scala.utils.Log

// User should extends BackfillerPlugin
abstract class BackfillerPlugin[T <: Entity, Args <: BackfillerArgs, SourceArg](val cmdLine: Args) extends BackfillerPluginBase {
  def sliceProvider: SliceProvider[Args, SourceArg]
  def sourceProvider: SourceProvider[T, SourceArg]
  def convertProvider: ConvertProvider[T]
}

// this trait should only place common default implement
trait BackfillerPluginBase {
  val cmdLine: BackfillerArgs
  def sinkProvider = new DefaultSinkProvider(cmdLine)
}

class BaseBackfillerPlugin[Args <: BackfillerArgs](plugin: BackfillerPlugin[Entity, Args, Any],override val cmdLine: Args) extends BackfillerPlugin[Entity, Args, Any](cmdLine) with Log {
  log.info("Instantiate BaseBackfillerPlugin primary construct.")

  def sliceProvider = plugin.sliceProvider
  def sourceProvider = plugin.sourceProvider
  def convertProvider = plugin.convertProvider
  override def sinkProvider = plugin.sinkProvider
}


