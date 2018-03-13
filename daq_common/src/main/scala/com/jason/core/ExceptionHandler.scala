package com.jason.core

import java.io.{File, PrintWriter}

import com.jason.utils.{Log, PluginExcutionException}
import com.jason.model.Phase.Phase
import com.jason.utils.AutoClose._
import com.jason.utils.PluginExcutionException

import scala.collection.mutable.ListBuffer

trait ExceptionHandler {
  def handle(ex: PluginExcutionException, phase: Phase): Unit
}

class FailLoggingExceptionHandler(failLoggingFile: Option[File]) extends ExceptionHandler with Log {

  private var failedCauseExceedMaxRetries = false

  val exceptionCache = new ListBuffer[(Phase, String)]()

  def hasExceptionOccur: Boolean = failedCauseExceedMaxRetries

  def handle(ex: PluginExcutionException, phase: Phase): Unit = {
    if (ex.tried == ex.maxRetry) {
      failedCauseExceedMaxRetries = true
    }
    exceptionCache += ((phase, ex.getMessage))
  }

  def logExceptionIfRequired(): Unit = {
    if (hasExceptionOccur) {
      if (failLoggingFile.isDefined) {
        using(new PrintWriter(failLoggingFile.get)) { pw =>
          exceptionCache foreach pw.print
        }
      } else {
        log.info("failLoggingFile is not defined, print fail info into console")
//        exceptionCache foreach log.info
      }
    }
  }
}
