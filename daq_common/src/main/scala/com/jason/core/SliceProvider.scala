package com.jason.core

/** Slice is for split source from multiple chunk, this is because:
  *  1. for performance concerns
  *  2. each acotr should take as less task as possible
  *  3. use multiple cpu parallel
  */
trait SliceProvider[Args <: BackfillerArgs, SourceArg] {
  def slice(args: Args): Seq[SourceArg]
}
