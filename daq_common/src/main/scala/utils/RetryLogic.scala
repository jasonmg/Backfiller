package main.scala.utils

import main.scala.core.ExceptionHandler
import main.scala.model.Phase.Phase

import scala.util.Try
import scala.util.control.NonFatal


object RetryLogic extends Log {

  def retry[In, Out](arg: In, f: In => Out, phase: Phase, exceptionHandler: ExceptionHandler, tried: Int = 1, maxRetry: Int = 5, interval: Long = 1000): Option[Out] = {
    try {
      Some(f(arg))
    } catch {
      case NonFatal(ex) =>
        exceptionHandler.handle(PluginExcutionException(ex, tried, maxRetry), phase)
        if (tried >= maxRetry) {
          log.error(s"tried time: $tried >= $maxRetry")
          None
        } else {
          val intervalNext = interval + 10000 * tried
          log.warn(s"Phase: $phase, retry failed: ${tried} times, max: ${maxRetry}, sleep: ${intervalNext}ms before next try")
          log.warn(s"err msg: ${ex.getMessage}")
          Thread.sleep(intervalNext)
          retry(arg, f, phase, exceptionHandler, tried + 1, maxRetry, intervalNext)
        }
      case e: Throwable => log.fatal("Fatal error occurred, pay attention!"); throw e
    }
  }

  def actionWithRetry[Out](f: => Out, phase: Phase, exceptionHandler: ExceptionHandler, tried: Int = 0, maxRetry: Int = 5, interval: Long = 30000): Option[Out] = {
    retry(None, (x: Option[Any]) => f, phase, exceptionHandler, tried, maxRetry, interval)
  }

  def tryOnce[Out](f: => Out): Out = {
    f
  }

}

case class PluginExcutionException(cause: Throwable, tried: Int, maxRetry: Int) extends Exception(s"Attempt: $tried of $maxRetry", cause)
