package main.scala.actor

import akka.actor._
import com.codahale.metrics.MetricRegistry

class Statistic extends Actor with ActorLogging {
  import Statistic._

  val registry = new MetricRegistry()

  val name = MetricRegistry.name(classOf[Statistic], _: String)


  val sliceMeter = registry.meter(name("slice"))
  val sourceMeter = registry.meter(name("source"))
  val filterMeter = registry.meter(name("filter"))
  val convertMeter = registry.meter(name("convert"))
  val sinkMeter = registry.meter(name("sink"))

  def receive = {

    case SliceRecord=>
      sliceMeter.mark()
    case SourceRecord=>
      sourceMeter.mark()
    case FilterRecord=>
      filterMeter.mark()
    case ConvertRecord=>
      convertMeter.mark()
    case SinkRecord=>
      sinkMeter.mark()

    case Print =>
      log.info("====================================")
      log.info(s"slice: ${sliceMeter.getCount}")
      log.info(s"source: ${sourceMeter.getCount}")
      log.info(s"filter: ${filterMeter.getCount}")
      log.info(s"convert: ${convertMeter.getCount}")
      log.info(s"sink: ${sinkMeter.getCount}")
  }
}

object Statistic {
  case object SourceRecord
  case object SliceRecord
  case object FilterRecord
  case object ConvertRecord
  case object SinkRecord

  case object Print

  def props =  Props(new Statistic)

}
