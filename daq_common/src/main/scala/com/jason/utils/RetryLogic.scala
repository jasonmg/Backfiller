package com.jason.utils

import com.jason.core.ExceptionHandler
import com.jason.model.Phase.Phase

import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}


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
      case e: Throwable => log.error("Fatal error occurred, pay attention!"); throw e
    }
  }

  def actionWithRetry[Out](f: => Out, phase: Phase, exceptionHandler: ExceptionHandler, tried: Int = 0, maxRetry: Int = 5, interval: Long = 30000): Option[Out] = {
    retry(None, (x: Option[Any]) => f, phase, exceptionHandler, tried, maxRetry, interval)
  }

  def tryOpOnce[R1, R2](f: => R1, phase: Phase, exceptionHandler: ExceptionHandler)(success: R1 => R2): Try[R2] =
    Try(f) match {
      case Success(v) =>
        Success(success(v))
      case Failure(ex) =>
        exceptionHandler.handle(PluginExcutionException(ex, 1, 1), phase)
        Failure(ex)
    }
}

case class PluginExcutionException(cause: Throwable, tried: Int, maxRetry: Int) extends Exception(s"Attempt: $tried of $maxRetry", cause)
