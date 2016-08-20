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
    // we should provide better time format than this now.
    // given extra precision on the result, e.g. if we want precision to Second level
    // then for the case which time is less than 1 second, we should auto extend precision to millisecond, i.e. 0.013 s,
    // others great than 1 second, should keep it normal. i.e. 3 s.
    def nano2Millis(nanoSeconds: Long): String = {
      nanoSeconds / 1000 / 1000 +" ms"
    }

    def nano2Seconds(nanoSeconds: Long): String = {
      nanoSeconds / 1000 / 1000 / 1000 + " s"
    }
  }

}
