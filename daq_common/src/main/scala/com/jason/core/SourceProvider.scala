package com.jason.core

/**
  * @author mingjiang.ji on 2018/3/12
  */
trait SourceProvider[In, SourceType] {
  def load(sourceArg: SourceType): Seq[In]
}
