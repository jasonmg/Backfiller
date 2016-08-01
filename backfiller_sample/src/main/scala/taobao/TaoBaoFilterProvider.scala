package main.scala.taobao

import main.scala.core.FilterProvider
import main.scala.model.TaoBaoEntity

class TaoBaoFilterProvider extends FilterProvider[TaoBaoEntity] {
  def filter(args: Seq[TaoBaoEntity]):Seq[TaoBaoEntity] ={
    args.filter(_.age>20)
  }
}
