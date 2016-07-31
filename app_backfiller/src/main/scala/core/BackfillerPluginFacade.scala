package main.scala.core

import main.scala.utils.Log

/**
  * this is for protect plugin provider method *NEW* in actor system at each call
  * since we invoke def in actor, and each time it will re-new the provider class
  * i.e. [[plugin.sinkProvider]] it will cause DefaultSinkProvider initialise at ever sink actor
  */
class BackfillerPluginFacade[Args <: BackfillerArgs](plugin: BackfillerPlugin[Any , Args, Any]) extends Log{

  // use val here is because make sure the provider initialise only once
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