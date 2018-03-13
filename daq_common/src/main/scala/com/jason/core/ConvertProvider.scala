package com.jason.core

import com.jason.model.EntityCollection

/**
  * @author mingjiang.ji on 2018/3/12
  */
trait ConvertProvider[In] {
  def convert(source: Seq[In]): EntityCollection
}
