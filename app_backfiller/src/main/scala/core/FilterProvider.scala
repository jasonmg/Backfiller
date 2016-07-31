package main.scala.core

trait FilterProvider[In] {
  def filter(entities: Seq[In]): Seq[In]
}
