package main.scala.actor

import akka.actor.{Props, _}
import main.scala.actor.Slice._
import main.scala.core._

object Controller {
  case object AllStart
  case object AllComplete
  case object StartSlice
  case object SliceComplete
  case object StartSink
  case object SinkComplete
  case object StartSource
  case object SourceComplete
  case object StartFilter
  case object FilterComplete
  case object StartConverter
  case object ConverterComplete

  case object ShutDown

}


/**
  * It's the controller class to coordinate the work flow.
  */
class Controller[CmdLineArgs <: BackfillerArgs](plugin: BackfillerPluginFacade[CmdLineArgs]) extends Actor with ActorLogging {
  import Controller._

  def actor(props: Props, name: String) = context.watch(context.actorOf(props, name))

  val sink = actor(Sink.props(plugin, self), "Sink_actor")
  val converter = actor(Converter.props(plugin, sink), "Converter_actor")
  val filter = actor(Filter.props(plugin,converter), "Filter_actor")
  val source = actor(Source.props(plugin, filter), "Source_actor")
  val slice = actor(Slice.props(plugin, self, source), "Slice_actor")

  def receive = {
    case AllStart =>
      source ! StartSource
      converter ! StartConverter
      filter ! StartFilter
      sink ! StartSink
      slice ! StartSlice

    case StartSlice =>
      slice ! RequestSlice

    case AllSliceSent =>
      slice ! ShutDown

    case ShutDown =>
      log.info("forward controller shutdown to source")
      source ! ShutDown

    case SourceComplete =>
      filter ! ShutDown

    case FilterComplete=>
      converter ! ShutDown

    case ConverterComplete =>
      sink ! ShutDown

    case SinkComplete =>
      log.info(s"sink succeed")

    case StartConverter => log.info("start converter done")
    case StartSink => log.info("start sink done")
    case StartSource => log.info("start source done")
    case StartFilter => log.info("start filter done")

    case Terminated(deadActor: ActorRef) => context.children match {
      case Child() => self ! AllComplete
      case _ => log.info(s"Actor: $deadActor terminated, remain active actor are: ${context.children}")
    }

    case AllComplete =>
      log.info("all completed.")
      context.system.shutdown()
  }


}

object Child {
  def unapplySeq(child: Iterable[ActorRef]) = Some(child.toSeq)
}