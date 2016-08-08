package main.scala.actor

import java.util.concurrent.TimeUnit

import akka.actor._
import com.codahale.metrics.{Clock, Counter, MetricRegistry, Timer}
import main.scala.actor.Statistic.RecordFilterTime


object Statistic {

  case object SystemStart

  case object RecordSlice

  case class RecordSliceTime(time: Long)

  case object RecordSource

  case class RecordSourceTime(time: Long)

  case class RecordFilter(originSize: Long, filterSize: Long)

  case class RecordFilterTime(time: Long)

  case object RecordConvert

  case class RecordConvertTime(time: Long)

  case class RecordInsert(num: Int)

  case class RecordFlush(time: Long)

  case object Print

  def props =  Props(new Statistic)
}

class Statistic extends Actor with ActorLogging {
  import Statistic._

  val registry = new MetricRegistry()
  val name = MetricRegistry.name(classOf[Statistic], _: String)

  var systemStartTime = 0L
  val clock = Clock.defaultClock

  val sliceTime = new Timer()
  registry.register("sliceT", sliceTime)
  val sliceCount = new Counter()
  registry.register("sliceC", sliceCount)
  val sourceTime = new Timer()
  registry.register("sourceT", sourceTime)
  val sourceCount = new Counter()
  registry.register("sourceC", sourceCount)

  val filterTime = new Timer()
  registry.register("filterT", filterTime)
  val filterCountOriginal = new Counter()
  registry.register("filterCountOriginal", filterCountOriginal)
  val filterCountAfter = new Counter()
  registry.register("filterCountAfter", filterCountAfter)

  val convertTime = new Timer()
  registry.register("convertT", convertTime)
  val convertCount = new Counter()
  registry.register("convertC", convertCount)

  val flushTime = new Timer()
  registry.register("flushT", flushTime)
  val sinkCount = new Counter()
  registry.register("sinkC", sinkCount)

  def receive = {

    case SystemStart =>
      systemStartTime = clock.getTick

    case RecordSlice =>
      sliceCount.inc()

    case RecordSliceTime(t) =>
      sliceTime.update(t, TimeUnit.NANOSECONDS)

    case RecordSource =>
      sourceCount.inc()

    case RecordSourceTime(t) =>
      sourceTime.update(t, TimeUnit.NANOSECONDS)

    case RecordFilter(os, fs) =>
      filterCountOriginal.inc(os)
      filterCountAfter.inc(fs)

    case RecordFilterTime(t) =>
      filterTime.update(t, TimeUnit.NANOSECONDS)

    case RecordConvert =>
      convertCount.inc()

    case RecordConvertTime(t) =>
      convertTime.update(t, TimeUnit.NANOSECONDS)

    case RecordFlush(t) =>
      flushTime.update(t, TimeUnit.NANOSECONDS)

    case RecordInsert(num) =>
      sinkCount.inc(num)

    case Print =>
      val elapsed = clock.getTick - systemStartTime
      println(s"==========elapsed: $elapsed==========")


  }
}

