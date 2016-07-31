package main.scala.actor

import akka.actor._
import main.scala.actor.Controller._
import main.scala.actor.Converter.RequestConverter
import main.scala.core._
import main.scala.utils.RetryLogic._

/**
  * Source response for read data from diverse resource system
  */
class Source(plugin: BackfillerPluginFacade[_], convertActor: ActorRef) extends Actor with ActorLogging {

  import Source._

  def receive = {
    case StartSource =>
      log.info("start source actor.")
      sender() ! StartSource
    case RequestSource(arg) =>
      log.info(s"request source from: ${plugin.getClass.getName}")
      val res = retry(arg, plugin.sourceProvider.load)

      convertActor ! RequestConverter(res.toSeq)


    case Controller.ShutDown =>
      sender() ! SourceComplete
      context.stop(self)
  }
}

object Source {
  def props(plugin: BackfillerPluginFacade[_], convertActor: ActorRef) =
    Props(new Source(plugin, convertActor))


  case class RequestSource(sourceArg: Any)

}