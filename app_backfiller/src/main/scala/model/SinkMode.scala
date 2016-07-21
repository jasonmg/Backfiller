package main.scala.model

object SinkMode extends Enumeration {
  type SinkMode  = Value
  val JSON,XML,CSV = Value
}
