package main.scala.core

/**
  * Created by Administrator on 2016-06-30.
  */
trait SliceProvider[A <: BackfillerArgs, SourceArg] {
  def slice(args: A): Seq[SourceArg]
}
