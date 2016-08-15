package main.scala.core.continuous

import main.scala.core.BackfillerArgs
import org.kohsuke.args4j
import scala.concurrent.duration.FiniteDuration

class BackfillerContinuousArgs extends BackfillerArgs{
  import BackfillerContinuousArgs._

  @args4j.Option(name = "--duration", usage = "run time limit, since continuous backfill won't stop unless manually kill, otherwise given time limit for stop" +
    "format: e.g. 1 minute",
    required = false)
  def setDuration(name: String): Unit ={
    val fdMath(length,unit) = name.toLowerCase()
    duration = Some(FiniteDuration(length.toLong, unit))
  }
  var duration: Option[FiniteDuration] = None

}

object BackfillerContinuousArgs{
  val fdMath = """(\d+)\s*(day|days|hour|hours|minute|minutes|second|seconds|millisecond|milliseconds{1})""".r
}
