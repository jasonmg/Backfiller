package main.scala.core

import main.scala.utils.Log

// User should extends BackfillerPlugin
abstract class BackfillerPlugin[T <: Entity, Args <: BackfillerArgs, SourceArg](val cmdLine: BackfillerArgs) extends BackfillerPluginBase {
  def sliceProvider: SliceProvider[Args, SourceArg]
  def sourceProvider: SourceProvider[T, SourceArg]
  def convertProvider: ConvertProvider[T]
}

// this trait should only place common default implement
trait BackfillerPluginBase {
  val cmdLine: BackfillerArgs
  def sinkProvider = new DefaultSinkProvider(cmdLine)
}

class BaseBackfillerPlugin[Args <: BackfillerArgs](plugin: BackfillerPlugin[Entity, Args, Any], val args: BackfillerArgs) extends BackfillerPlugin[Entity, Args, Any](args) with Log {
  log.info("Instantiate BaseBackfillerPlugin primary construct.")

  override val cmdLine: BackfillerArgs = args
  def sliceProvider = plugin.sliceProvider
  def sourceProvider = plugin.sourceProvider
  def convertProvider = plugin.convertProvider
  override def sinkProvider = plugin.sinkProvider
}


