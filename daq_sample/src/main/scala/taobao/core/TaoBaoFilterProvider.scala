package main.scala.taobao.core

import main.scala.core.FilterProvider
import main.scala.taobao.model.TaoBaoEntity

class TaoBaoFilterProvider extends FilterProvider[TaoBaoEntity] {
  def filter(args: Seq[TaoBaoEntity]): Seq[TaoBaoEntity] = {
//    Thread.sleep(10)
//    args.filter(e => e.age >= 20 && e.age < 25 && e.name == "Ji Xing")
    args
  }
}
