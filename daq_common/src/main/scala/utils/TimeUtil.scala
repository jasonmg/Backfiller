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

}
