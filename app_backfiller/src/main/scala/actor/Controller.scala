package main.scala.actor

import akka.actor._
import main.scala.actor.Source.RequestSource
import main.scala.core._

/**
  * It's the controller class to coordinate the work flow.
  */
class Controller(plugin: BaseBackfillerPlugin) extends Actor with ActorLogging {
  import Controller._

  val source = context.actorOf(Source.props(plugin),"Source_actor")
  val converter = context.actorOf(Converter.props(plugin),"Converter_actor")
  val sink = context.actorOf(Sink.props(plugin),"Sink_actor")

  def receive = {
    case AllStart =>
      source ! StartSource
      converter ! StartConverter
      sink ! StartSink

    case StartSourceDone=>
      source ! RequestSource


    case StartConverterDone => log.info("start converter done")
    case StartSinkDone => log.info("start sink done")


    case AllComplete =>
      log.info("all completed.")

  }


}

object Controller {

  case object AllStart
  case object AllComplete

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
