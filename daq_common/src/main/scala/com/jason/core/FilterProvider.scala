package com.jason.core

// TODO(https://github.com/jasonmg/daq/issues/6) ideally, we should provide default filter logic
trait FilterProvider[In] {
  def filter(entities: Seq[In]): Seq[In]
}

trait DefaultFilterProvider[In] extends FilterProvider[In]{
  def filter(entities: Seq[In]): Seq[In] = entities
}

