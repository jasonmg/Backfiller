package main.scala.core

trait FilterProvider[In] {
  def filter(entities: Traversable[In]): Seq[In]
}
