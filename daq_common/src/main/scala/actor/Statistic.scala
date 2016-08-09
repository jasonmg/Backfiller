package main.scala.actor

import java.util.concurrent.TimeUnit

import akka.actor._
import com.codahale.metrics.{Clock, Counter, MetricRegistry, Timer}
import main.scala.actor.Statistic.RecordFilterTime
import main.scala.model.Table


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

  def props = Props(new Statistic)
}

class Statistic extends Actor with ActorLogging {

  import Statistic._

  val registry = new MetricRegistry()
  val name = MetricRegistry.name(classOf[Statistic], _: String)

  var systemStartTime = 0L
  val clock = Clock.defaultClock

  val sliceTime = new Timer()
  registry.register(name("sliceT"), sliceTime)
  val sliceCount = new Counter()
  registry.register(name("sliceC"), sliceCount)
  val sourceTime = new Timer()
  registry.register(name("sourceT"), sourceTime)
  val sourceCount = new Counter()
  registry.register(name("sourceC"), sourceCount)

  val filterTime = new Timer()
  registry.register(name("filterT"), filterTime)
  val filterCountOriginal = new Counter()
  registry.register(name("filterCountOriginal"), filterCountOriginal)
  val filterCountAfter = new Counter()
  registry.register(name("filterCountAfter"), filterCountAfter)

  val convertTime = new Timer()
  registry.register(name("convertT"), convertTime)
  val convertCount = new Counter()
  registry.register(name("convertC"), convertCount)

  val flushTime = new Timer()
  registry.register(name("flushT"), flushTime)
  val sinkCount = new Counter()
  registry.register(name("sinkC"), sinkCount)

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
      val elapsed = toMillis(clock.getTick - systemStartTime)
      println(s"========== elapsed so far: $elapsed ==========")
      val s = sliceTime.getSnapshot

      println(sliceCount.getCount+ " "+ sliceTime.getCount+" "+sliceTime.getMeanRate + " " + sliceTime.getOneMinuteRate+" "
      + sliceTime.getFiveMinuteRate+" "+ sliceTime.getFifteenMinuteRate + " " + s.get75thPercentile()
      +" " + s.get95thPercentile() + " " + s.get99thPercentile())

  }

  def printTable() = {
    val sliceParam = PrintParam("slice", sliceTime, sliceCount.getCount, sliceCount.getCount, 0)
    val sourceParam = PrintParam("source", sourceTime, sourceCount.getCount, sourceCount.getCount, 0)
    val filterParam = PrintParam("filter", filterTime, filterCountOriginal.getCount, filterCountAfter.getCount, 0)
    val convertParam = PrintParam("convert", convertTime, convertCount.getCount, convertCount.getCount, 0)
    val sinkParam = PrintParam("sink", flushTime, sinkCount.getCount, sinkCount.getCount, 0)

    val head = Seq("phase","before","after","failure","MeanRate","OneMinuteRate",
    "FiveMinuteRate","FifteenMinuteRate","Max","Mean","Min","75thPercentile","95thPercentile","99thPercentile")

    val table = Table(head)
      .addRow(buildRow(sliceParam))
      .addRow(buildRow(sourceParam))
      .addRow(buildRow(filterParam))
      .addRow(buildRow(convertParam))
      .addRow(buildRow(sinkParam))
  }

  def buildRow(param: PrintParam): Seq[String] = {
    val timer = param.timer
    val s = timer.getSnapshot
    val row = Seq(param.phase,param.phaseStart,param.phaseEnd,param.phaseFailure,timer.getMeanRate,timer.getOneMinuteRate,
      timer.getFiveMinuteRate,timer.getFifteenMinuteRate,s.getMax,s.getMean,s.getMin,s.get75thPercentile(),
      s.get95thPercentile(),s.get99thPercentile()).map(_.toString)

    row
  }

  def toMillis(nanoseconds: Long): String = {
    nanoseconds / 1000 / 1000 +" ms"
  }
}

case class PrintParam(phase: String,timer: Timer, phaseStart: Long, phaseEnd: Long, phaseFailure: Long)

