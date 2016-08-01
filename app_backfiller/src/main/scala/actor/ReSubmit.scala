package main.scala.actor

import akka.actor._

object ReSubmit {
  case object RestartHead
  case object RestartTail

  sealed trait RestartStatus
  case object NotRestarting extends RestartStatus
  case object AwaitReplayMessage extends RestartStatus
  case object ReplayMessage extends RestartStatus
}

trait ReSubmit extends Actor with ActorLogging with Stash {
  import ReSubmit._

  private var statusRestart: RestartStatus = NotRestarting

  final def receive: Receive = replayAfterRestart.orElse(_receive)

  def replayAfterRestart: Receive = {
    case RestartHead =>
      log.info("Ready to replay stashed message to actor mailbox by order")
      statusRestart = ReplayMessage
    case RestartTail if statusRestart == ReplayMessage =>
      log.info("ReSubmit Stashed Message")
      unstashAll()
      statusRestart = NotRestarting
    case RestartTail if statusRestart == AwaitReplayMessage =>
      log.warning(s"Status: $statusRestart not correct, may be something wrong during preRestart, please investigate.")
    case msg if statusRestart == AwaitReplayMessage =>
      log.info(s"Actor resumed, but status is AwaitReplayMessage thus stash msg: $msg")
      stash()
  }

  def _receive: Receive

  def restartStore(): Unit = {}

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"step in preRestart phase, actor restart cause by msg: $message")
    self ! RestartHead

    restartStore()

    message match {
      case Some(msg) =>
        log.warning(s"Resend message: $msg to mailbox")
        self ! msg

      case None => log.error(s"Actor restart cause by throwable issues: ${reason.getMessage}")
    }

    self ! RestartTail
  }

  override  def postRestart(reason: Throwable): Unit = {
    statusRestart = AwaitReplayMessage
  }
}

