package com.jason.core

// User should extends BackfillerPlugin for implementation
abstract class BackfillerPlugin[In, Args <: BackfillerArgs, SourceType](val cmdLine: Args) extends BackfillerPluginBase {
  def sliceProvider: SliceProvider[Args, SourceType]
  def sourceProvider: SourceProvider[In, SourceType]
  def convertProvider: ConvertProvider[In]
  def filterProvider: FilterProvider[In] = new DefaultFilterProvider[In]{}

  // TODO(https://github.com/jasonmg/daq/issues/7) ideally, sink logic should move to backfillerpluginbase
  def sinkProvider: SinkProvider = new DefaultSinkProvider(cmdLine)
}

// all common default implementation should be placed here
trait BackfillerPluginBase {
  val cmdLine: BackfillerArgs
  def exceptionHandler: ExceptionHandler = new FailLoggingExceptionHandler(cmdLine.failLogFile)
  def onComplete: Unit = {}
  def isContinuous: Boolean = false
}

trait BackfillerPluginCompanion[In, Args <: BackfillerArgs, SourceArg] {
  def apply(cmdLine: Args): BackfillerPlugin[In, Args, SourceArg]
  def pluginIdentifier: String
}





