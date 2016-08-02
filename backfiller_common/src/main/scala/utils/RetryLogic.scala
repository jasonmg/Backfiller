package main.scala.utils

import scala.util.control.NonFatal


object RetryLogic extends Log {

  def retry[In, Out](arg: In, f: In => Out, tried: Int = 0, maxReTry: Int = 5, interval: Long = 30000): Out = {
    try {
      f(arg)
    } catch {
      case NonFatal(ex) =>
        if (tried >= maxReTry) {
          log.error(s"tried time: $tried >= $maxReTry, thus throw exception.")
          throw ex
        } else {
          val intervalNext = interval + 10000 * tried
          log.warn(s"retry failed: ${tried} times, max: ${maxReTry}, sleep: ${intervalNext}ms before next try")
          Thread.sleep(intervalNext)
          retry(arg, f, tried + 1, maxReTry, intervalNext)
        }
      case e @ _ => log.fatal("Fatal error occurred, pay attention!"); throw e
    }
  }

  def tryOnce[Out](f: => Out): Out = {
    f
  }

}
