package main.scala.core


trait SourceProvider[In, SourceType] {
  def load(sourceArg: SourceType): Seq[In]
}
