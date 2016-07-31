package main.scala.taobao

import main.scala.core.ConvertProvider
import main.scala.model.{EntityCollection, TaoBaoEntity}


class TaoBaoConvertProvider extends ConvertProvider[TaoBaoEntity] {

  def convert(source: Seq[TaoBaoEntity]): EntityCollection =
    EntityCollection(source)
}
