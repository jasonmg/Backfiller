package main.scala.actor

import akka.actor._
import com.codahale.metrics.{Clock, MetricRegistry}


object Statistic {
  case object SystemStart

  case object SourceRecord
  case object SliceRecord
  case class FilterRecord(originSize: Long, filterSize: Long)
  case object ConvertRecord
  case object SinkRecord

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

  var originSize = 0L
  var filterSize = 0L
  val sliceMeter = registry.meter(name("slice"))
  val sourceMeter = registry.meter(name("source"))
  val filterMeter = registry.meter(name("filter"))
  val convertMeter = registry.meter(name("convert"))
  val sinkMeter = registry.meter(name("sink"))

  def receive = {

    case SystemStart =>
      systemStartTime = clock.getTick

    case SliceRecord =>
      sliceMeter.mark()

    case SourceRecord =>
      sourceMeter.mark()

    case FilterRecord(os, fs) =>
      originSize += os
      filterSize += fs
      filterMeter.mark()

    case ConvertRecord =>
      convertMeter.mark()

    case SinkRecord =>
      sinkMeter.mark()

    case Print =>
      val elapsed = clock.getTick - systemStartTime
      println(s"==========elapsed: $elapsed==========")

      println(s"slice: ${sliceMeter.getCount}, ${sliceMeter.getOneMinuteRate}, ${sliceMeter.getFiveMinuteRate}")
      println(s"source: ${sourceMeter.getCount}")
      println(s"filter: ${filterMeter.getCount}")
      println(s"convert: ${convertMeter.getCount}")
      println(s"sink: ${sinkMeter.getCount}")
  }
}

