package main.scala.actor

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Props, _}
import main.scala.actor.Slice._
import main.scala.core._
import akka.routing.{Broadcast, RoundRobinPool}

import scala.concurrent.duration._

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

  case class ScheduleShutDown(length: FiniteDuration)

}


/**
  * It's the controller class to coordinate the work flow.
  */
class Controller[CmdLineArgs <: BackfillerArgs](plugin: BackfillerPluginFacade[CmdLineArgs],
                                                batchSize: Int) extends Actor with ActorLogging {

  import Controller._

  def actor(props: Props, name: String) = context.watch(context.actorOf(props, name))

  val router = RoundRobinPool

  val statistic = actor(Statistic.props, "Statistic_actor")
  val sink = actor(Sink.props(plugin, batchSize, self, statistic).withDispatcher("sink-dispatcher").withRouter(router(4)), "Sink_actor")
  val converter = actor(Converter.props(plugin, sink, statistic).withDispatcher("converter-dispatcher").withRouter(router(4)), "Converter_actor")
  val filter = actor(Filter.props(plugin, self, converter, statistic).withDispatcher("filter-dispatcher").withRouter(router(4)), "Filter_actor")
  val source = actor(Source.props(plugin, filter, statistic).withDispatcher("source-dispatcher").withRouter(router(4)), "Source_actor")
  val slice = actor(Slice.props(plugin, self, source, statistic), "Slice_actor")

  import scala.concurrent.ExecutionContext.Implicits.global
  statistic ! Statistic.SystemStart
  context.system.scheduler.schedule(1 minute, 1 minute, statistic, Statistic.Print)

  val source_active = new AtomicInteger()
  val filter_active = new AtomicInteger()
  val converter_active = new AtomicInteger()
  val sink_active = new AtomicInteger()

  def receive = {
    case msg @ ScheduleShutDown(length) =>
      log.info(s"start a timer to shutdown system, duration: $length")
      context.system.scheduler.scheduleOnce(length, slice, msg)

    case AllStart =>
      source ! Broadcast(StartSource)
      converter ! Broadcast(StartConverter)
      filter ! Broadcast(StartFilter)
      sink ! Broadcast(StartSink)
      slice ! StartSlice

    case StartSlice =>
      slice ! RequestSlice

    case AllSliceSent =>
      slice ! ShutDown

    case ShutDown =>
      log.info("forward controller shutdown to source actor")
      source ! Broadcast(ShutDown)

    case SourceComplete =>
      val active = source_active.decrementAndGet()
      log.info(s"complete source actor, remaining active: ${active}")
      if(active == 0) filter ! Broadcast(ShutDown)

    case FilterComplete =>
      val active = filter_active.decrementAndGet()
      log.info(s"complete filter actor, remaining active: ${active}")
      if(active == 0) converter ! Broadcast(ShutDown)

    case ConverterComplete =>
      val active = converter_active.decrementAndGet()
      log.info(s"complete converter actor, remaining active: ${active}")
      if(active == 0) sink ! Broadcast(ShutDown)

    case SinkComplete =>
      val active = sink_active.decrementAndGet()
      log.info(s"complete sink actor, remaining active: ${active}")

    case StartConverter =>
      log.info(s"start converter actor, active: ${converter_active.incrementAndGet()}")
    case StartSink =>
      log.info(s"start sink actor, active: ${sink_active.incrementAndGet()}")
    case StartSource =>
      log.info(s"start source actor, active: ${source_active.incrementAndGet()}")
    case StartFilter =>
      log.info(s"start filter actor, active: ${filter_active.incrementAndGet()}")

    case Terminated(deadActor: ActorRef) => context.children match {
      case Child(stat) if(stat eq statistic) =>
        statistic ! Statistic.Print
        self ! AllComplete
      case Child() => log.error("this is should not happend right now.")//self ! AllComplete
      case _ => log.info(s"Actor: $deadActor terminated, remain active actor are: ${context.children}")
    }

    case AllComplete =>
      log.info("all completed. shutdown system")
      context.system.shutdown()
  }

}

object Child {
  def unapplySeq(child: Iterable[ActorRef]) = Some(child.toSeq)
}