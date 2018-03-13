package com.jason.core

import com.jason.utils.Log

/**
  * this is for protect plugin provider method *NEW* in actor system at each call
  * since we invoke def in actor, and each time it will re-new the provider class
  * i.e. [[plugin.sinkProvider]] it will cause DefaultSinkProvider initialise at ever sink actor
  */
class BackfillerPluginFacade[Args <: BackfillerArgs](plugin: BackfillerPlugin[Any , Args, Any]) extends Log{

  // use val here is because make sure the provider initialise only once
  private val _sinkProvider = plugin.sinkProvider
  private val _sliceProvider = plugin.sliceProvider
  private val _sourceProvider = plugin.sourceProvider
  private val _convertProvider = plugin.convertProvider
  private val _filterProvider = plugin.filterProvider
  private val _exceptionHandler = plugin.exceptionHandler

  val isContinuous = plugin.isContinuous
  val cmdLine: Args = plugin.cmdLine
  def sliceProvider = _sliceProvider
  def sourceProvider = _sourceProvider
  def convertProvider = _convertProvider
  def sinkProvider = _sinkProvider
  def filterProvider = _filterProvider
  def exceptionHandler = _exceptionHandler
  def onComplete = plugin.onComplete
}
