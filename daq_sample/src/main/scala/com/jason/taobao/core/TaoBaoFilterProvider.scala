package com.jason.taobao.core

import com.jason.core.FilterProvider
import com.jason.taobao.model.TaoBaoEntity

class TaoBaoFilterProvider extends FilterProvider[TaoBaoEntity] {
  def filter(args: Seq[TaoBaoEntity]): Seq[TaoBaoEntity] = {
//    Thread.sleep(10)
    args.filter(e => e.age >= 20 && e.age < 25 && e.name == "Ji Xing")
//    args
  }
}
