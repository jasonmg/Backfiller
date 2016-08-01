package main.scala.taobao.core

import main.scala.core.FilterProvider
import main.scala.taobao.model.TaoBaoEntity

class TaoBaoFilterProvider extends FilterProvider[TaoBaoEntity] {
  def filter(args: Seq[TaoBaoEntity]):Seq[TaoBaoEntity] ={
    args.filter(_.age>20)
  }
}
