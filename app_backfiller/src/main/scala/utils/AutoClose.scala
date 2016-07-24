package main.scala.utils

import scala.util.control.NonFatal

object AutoClose extends Log {

  def using[A, T <: {def close() : Unit}](closeable: T)(f: T => A): A = {
    def closeQuality(closeable: T): Unit = {
      try {
        closeable.close()
      } catch {
        case NonFatal(exc) => log.error(s"try close resource with exception: $exc")
      }
    }

    try {
      f(closeable)
    } finally {
      closeQuality(closeable)
    }
  }
}
