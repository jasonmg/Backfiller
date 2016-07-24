package main.scala.core


trait SliceProvider[Args <: BackfillerArgs, +SourceArg] {
  def slice(args: Args): Seq[SourceArg]
}
