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
    import main.scala.utils.StrUtil._

    implicit class nano(nanoSeconds: Long){
      def toMicos =  nanoSeconds / 1000
      def toMillis = toMicos / 1000
      def toSeconds = toMillis / 1000
      def toMinutes = toSeconds / 60
    }

    implicit def long2Str(l: Long) = l.toString

    def nano2Millis(nanoSeconds: Long): String = {
      val s = nanoSeconds.toMillis
      val res = {
        if (s != 0) s+" " +readableFormat(s)
        else {
          "0." + padding(nanoSeconds.toMicos % 1000)
        }
      } + " ms"
      res
    }

    def nano2Seconds(nanoSeconds: Long): String = {
      val s = nanoSeconds.toSeconds
      val res = {
        if (s != 0) readableFormat(s)
        else {
          "0." + padding(nanoSeconds.toMillis % 1000)
        }
      } + " s"
      res
    }
  }

}
