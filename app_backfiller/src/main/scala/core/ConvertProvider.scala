package main.scala.core


trait ConvertProvider[T <: Entity] {
  def convert(source: T): EntityCollection
}
