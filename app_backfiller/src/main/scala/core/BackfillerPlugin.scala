package main.scala.core

import main.scala.utils.Log

trait BackfillerPlugin[T <: Entity, A <: BackfillerArgs, SourceArg] {

  def sliceProvider: SliceProvider[A, SourceArg]
  def sourceProvider: SourceProvider[T, SourceArg]
  def convertProvider: ConvertProvider[T]
  def sinkProvider: SinkProvider
}


// User should extends BaseBackfillerPlugin for any specific implementation
// because it contains show default implement, i.e. sinkProvider
class BaseBackfillerPlugin(plugin: BackfillerPlugin[Entity, BackfillerArgs, Any], args: BackfillerArgs) extends BackfillerPlugin[Entity, BackfillerArgs, Any] with Log {
  log.info("Instantiate BaseBackfillerPlugin primary construct.")

  def sliceProvider = plugin.sliceProvider
  def sourceProvider = plugin.sourceProvider
  def convertProvider = plugin.convertProvider
  def sinkProvider = new DefaultSinkProvider(args)
}


