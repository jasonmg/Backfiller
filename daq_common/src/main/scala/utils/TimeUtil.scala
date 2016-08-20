package main.scala.utils

import com.codahale.metrics.Clock

object TimeUtil {

  private val clock = Clock.defaultClock()

  def timer[A](f: => A): (Long, A) = {
    val start = clock.getTick
    val res = f
    val stop = clock.getTick
    (stop - start, res)
  }

  def readableTime = ReadableTime.nano2Millis _

  object ReadableTime {

    def nano2Millis(nanoSeconds: Long): String = {
      nanoSeconds / 1000 / 1000 +" ms"
    }

    def nano2Seconds(nanoSeconds: Long): String = {
      nanoSeconds / 1000 / 1000 / 1000 + " s"
    }
  }

}
