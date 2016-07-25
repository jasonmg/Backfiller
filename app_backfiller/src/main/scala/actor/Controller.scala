package main.scala.actor

import akka.actor._
import main.scala.actor.Slice.RequestSlice
import main.scala.core._

/**
  * It's the controller class to coordinate the work flow.
  */
class Controller[CmdLineArgs <: BackfillerArgs](plugin: BaseBackfillerPlugin[CmdLineArgs]) extends Actor with ActorLogging {

  import Controller._

  val source = context.actorOf(Source.props(plugin, converter), "Source_actor")
  val converter = context.actorOf(Converter.props(plugin, sink), "Converter_actor")
  val sink = context.actorOf(Sink.props(plugin, self), "Sink_actor")
  val slice = context.actorOf(Slice.props(plugin, self, source), "Slice_actor")

  def receive = {
    case AllStart =>
      slice ! StartSlice
      source ! StartSource
      converter ! StartConverter
      sink ! StartSink

    case StartSliceDone =>
      slice ! RequestSlice

    case SinkComplete =>
      log.info(s"sink successed")


    case StartConverterDone => log.info("start converter done")
    case StartSinkDone => log.info("start sink done")


    case AllComplete =>
      log.info("all completed.")

  }


}

object Controller {

  case object AllStart
  case object AllComplete

  case object StartSlice
  case object StartSliceDone
  case object SliceComplete

  case object StartSink
  case object StartSinkDone
  case object SinkComplete

  case object StartSource
  case object StartSourceDone
  case object SourceComplete

  case object StartConverter
  case object StartConverterDone
  case object ConverterComplete

}
