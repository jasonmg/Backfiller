package com.jason.model

object Phase extends Enumeration {
  type Phase = Value
  val Slice, Source, Filter, Convert, Sink = Value
}
