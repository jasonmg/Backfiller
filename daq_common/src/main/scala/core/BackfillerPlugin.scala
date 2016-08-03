package main.scala.core

// User should extends BackfillerPlugin for implementation
abstract class BackfillerPlugin[In, Args <: BackfillerArgs, SourceArg](val cmdLine: Args) extends BackfillerPluginBase {
  def sliceProvider: SliceProvider[Args, SourceArg]
  def sourceProvider: SourceProvider[In, SourceArg]
  def filterProvider: FilterProvider[In]
  def convertProvider: ConvertProvider[In]
}

// this trait should only place common default implement
trait BackfillerPluginBase {
  val cmdLine: BackfillerArgs
  def exceptionHandler: ExceptionHandler = new FailLoggingExceptionHandler(cmdLine.failLogFile)
  def onComplete: Unit = {}
  def sinkProvider = new DefaultSinkProvider(cmdLine)
}

trait BackfillerPluginCompanion[In, Args <: BackfillerArgs, SourceArg] {
  def apply(cmdLine: Args): BackfillerPlugin[In, Args, SourceArg]
  def pluginIdentifier: String
}





