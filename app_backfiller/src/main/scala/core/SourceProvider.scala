package main.scala.core


trait SourceProvider[T, SourceArg] {
  def load(sourceArg: SourceArg): Traversable[T]
}
