package main.scala.core

import main.scala.utils.Log

// User should extends BackfillerPlugin for implementation
abstract class BackfillerPlugin[In, Args <: BackfillerArgs, SourceArg](val cmdLine: Args) extends BackfillerPluginBase {
  def sliceProvider: SliceProvider[Args, SourceArg]
  def sourceProvider: SourceProvider[In, SourceArg]
  def convertProvider: ConvertProvider[In]
}

// this trait should only place common default implement
trait BackfillerPluginBase {
  val cmdLine: BackfillerArgs
  def onComplete: Unit = {}
  def sinkProvider = new DefaultSinkProvider(cmdLine)
}

trait BackfillerPluginCompanion[In, Args <: BackfillerArgs, SourceArg] {
  def apply(cmdLine: Args): BackfillerPlugin[In, Args, SourceArg]
  def pluginIdentifier: String
}


class BaseBackfillerPlugin[Args <: BackfillerArgs](plugin: BackfillerPlugin[Any , Args, Any]) extends Log{
  log.info("Instantiate BaseBackfillerPlugin primary construct.")

  // use val here is because make sure the provider initialise once
  val _sinkProvider = plugin.sinkProvider
  val _sliceProvider = plugin.sliceProvider
  val _sourceProvider = plugin.sourceProvider
  val _convertProvider = plugin.convertProvider

  val cmdLine: Args = plugin.cmdLine
  def sliceProvider = _sliceProvider
  def sourceProvider = _sourceProvider
  def convertProvider = _convertProvider
  def sinkProvider = _sinkProvider
  def onComplete = plugin.onComplete
}


