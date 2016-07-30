package main.scala.core


trait ConvertProvider[In] {
  def convert(source: Seq[In]): EntityCollection
}
