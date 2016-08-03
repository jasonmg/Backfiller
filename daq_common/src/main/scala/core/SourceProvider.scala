package main.scala.core


trait SourceProvider[In, SourceArg] {
  def load(sourceArg: SourceArg): Traversable[In]
}
