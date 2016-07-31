package main.scala.taobao

import main.scala.core.FilterProvider
import main.scala.model.TaoBaoCSVEntity

class TaoBaoFilterProvider extends FilterProvider[TaoBaoCSVEntity] {
  def filter(args: Seq[TaoBaoCSVEntity]):Seq[TaoBaoCSVEntity] ={
    args.filter(_.age>20)
  }
}
